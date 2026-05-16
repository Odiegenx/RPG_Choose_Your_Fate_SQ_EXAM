package dk.ek.gruppe2.chooseyourfate.repository.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryHasItemResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.ItemResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.LoadoutResponseDTO;
import dk.ek.gruppe2.chooseyourfate.enums.ItemType;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class Neo4jLoadoutRepositoryImpl implements Neo4jLoadoutRepository {

    private final Neo4jClient neo4jClient;

    public Neo4jLoadoutRepositoryImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    // Finder characterens inventory og alle items deri.
    // Henter derefter alle equipped items slot for slot og mapper det hele til en samlet loadout response.
    // Inventory skal altid findes, men det må gerne være tomt.
    @Override
    public LoadoutResponseDTO getLoadoutByCharacterId(Integer characterId) {
        Integer inventoryId = findInventoryId(characterId);
        List<InventoryItemData> inventoryItemData = findInventoryItems(characterId);

        List<InventoryHasItemResponseDTO> itemsInInventory = inventoryItemData.stream()
                .filter(itemData -> itemData.itemId() != null)
                .map(this::toInventoryHasItemResponseDTO)
                .toList();
        List<ItemResponseDTO> equippedItems = new ArrayList<>();

        findEquippedItem(characterId, ItemType.ARMOR_HEAD)
                .ifPresent(itemData -> equippedItems.add(toItemResponseDTO(itemData)));
        findEquippedItem(characterId, ItemType.ARMOR_CHEST)
                .ifPresent(itemData -> equippedItems.add(toItemResponseDTO(itemData)));
        findEquippedItem(characterId, ItemType.ARMOR_LEGS)
                .ifPresent(itemData -> equippedItems.add(toItemResponseDTO(itemData)));

        return new LoadoutResponseDTO(inventoryId, equippedItems, itemsInInventory);
    }

    // Finder alle items i characterens inventory sammen med amount på CONTAINS-relationen.
    // Hvis inventoryen er tom, returneres stadig inventoryen, men uden item-data.
    private List<InventoryItemData> findInventoryItems(Integer characterId) {
        return neo4jClient.query("""
                        MATCH (:Character {id: $characterId})-[:HAS_INVENTORY]->(inv:Inventory)
                        OPTIONAL MATCH (inv)-[r:CONTAINS]->(item:Item)
                        RETURN inv.id AS inventoryId,
                               item.id AS itemId,
                               item.name AS name,
                               item.description AS description,
                               item.type AS type,
                               r.amount AS amount
                        ORDER BY item.id
                        """)
                .bind(characterId).to("characterId")
                .fetchAs(InventoryItemData.class)
                .mappedBy((typeSystem, itemRecord) -> {
                    if (itemRecord.get("itemId").isNull()) {
                        return new InventoryItemData(
                                itemRecord.get("inventoryId").asInt(),
                                null,
                                null,
                                null,
                                null,
                                null
                        );
                    }

                    return new InventoryItemData(
                            itemRecord.get("inventoryId").asInt(),
                            itemRecord.get("itemId").asInt(),
                            itemRecord.get("name").asString(),
                            itemRecord.get("description").asString(),
                            ItemType.valueOf(itemRecord.get("type").asString().toUpperCase()),
                            itemRecord.get("amount").asInt()
                    );
                })
                .all()
                .stream()
                .toList();
    }

    @Override
    public Optional<ItemData> findEquippedItem(Integer characterId, ItemType itemType) {
        EquipmentSlotRelation relationType = resolveEquipmentRelation(itemType);

        String query = """
                MATCH (c:Character {id: $characterId})-[r:%s]->(item:Item)
                RETURN item.id AS itemId,
                       item.name AS name,
                       item.description AS description,
                       item.type AS type
                """.formatted(relationType.relationshipType());

        return neo4jClient.query(query)
                .bind(characterId).to("characterId")
                .fetchAs(ItemData.class)
                .mappedBy((typeSystem, itemRecord) -> new ItemData(
                        itemRecord.get("itemId").asInt(),
                        itemRecord.get("name").asString(),
                        itemRecord.get("description").asString(),
                        ItemType.valueOf(itemRecord.get("type").asString().toUpperCase())
                ))
                .one();
    }

    // Finder inventory-noden for den angivne character.
    // Inventory er en obligatorisk del af modellen, så der kastes fejl hvis den ikke findes.
    @Override
    public Integer findInventoryId(Integer characterId) {
        return neo4jClient.query("""
                    MATCH (:Character {id: $characterId})-[:HAS_INVENTORY]->(inventory:Inventory)
                    RETURN inventory.id AS inventoryId
                    """)
                .bind(characterId)
                .to("characterId")
                .fetchAs(Integer.class)
                .mappedBy((typeSystem, inventoryRecord) -> inventoryRecord.get("inventoryId").asInt())
                .one()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found for character id: " + characterId
                ));
    }

    // Oversætter itemets type til den slot-specifikke relationstype der bruges i Neo4j.
    // Hvis itemtypen ikke kan equips, kastes en fejl.
    private EquipmentSlotRelation resolveEquipmentRelation(ItemType itemType) {
        return switch (itemType) {
            case ARMOR_HEAD -> EquipmentSlotRelation.HEAD;
            case ARMOR_CHEST -> EquipmentSlotRelation.CHEST;
            case ARMOR_LEGS -> EquipmentSlotRelation.LEGS;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Item type is not supported for equipment"
            );
        };
    }

    // Har characterens inventory det her item, og hvor mange er der.
    // Vi returnerer 0 hvis itemet ikke er i inventory.
    @Override
    public Integer findInventoryItemAmount(Integer inventoryId, Integer itemId) {
        return neo4jClient.query("""
                    MATCH (inventory:Inventory {id: $inventoryId})-[relation:CONTAINS]->(item:Item {id: $itemId})
                    RETURN relation.amount AS amount
                    """)
                .bind(inventoryId).to("inventoryId")
                .bind(itemId).to("itemId")
                .fetchAs(Integer.class)
                .mappedBy((typeSystem, inventoryItemRecord) -> inventoryItemRecord.get("amount").asInt())
                .one()
                .orElse(0);
    }

    // Finder et item ud fra id og mapper det til intern read-model.
    // Bruges som validering, så vi ikke arbejder videre med et item der ikke findes.
    @Override
    public ItemData findItemById(Integer itemId) {
        return neo4jClient.query("""
                    MATCH (item:Item {id: $itemId})
                    RETURN item.id AS itemId,
                           item.name AS name,
                           item.description AS description,
                           item.type AS type
                    """)
                .bind(itemId).to("itemId")
                .fetchAs(ItemData.class)
                .mappedBy((typeSystem, itemRecord) -> new ItemData(
                        itemRecord.get("itemId").asInt(),
                        itemRecord.get("name").asString(),
                        itemRecord.get("description").asString(),
                        ItemType.valueOf(itemRecord.get("type").asString().toUpperCase())
                ))
                .one()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item not found with id: " + itemId
                ));
    }

    // Reducerer amount på relationen mellem inventory og item med 1.
    // Hvis amount er 1, bliver relationen slettet.
    @Override
    public void decrementInventoryItem(Integer inventoryId, Integer itemId) {
        neo4jClient.query("""
                    MATCH (:Inventory {id: $inventoryId})-[relation:CONTAINS]->(:Item {id: $itemId})
                    WITH relation, relation.amount AS oldAmount
                    FOREACH (_ IN CASE WHEN oldAmount > 1 THEN [1] ELSE [] END |
                        SET relation.amount = oldAmount - 1
                    )
                    FOREACH (_ IN CASE WHEN oldAmount = 1 THEN [1] ELSE [] END |
                        DELETE relation
                    )
                    """)
                .bind(inventoryId).to("inventoryId")
                .bind(itemId).to("itemId")
                .run();
    }

    // Tjekker om et item allerede er i inventory og lægger en til hvis det er,
    // ellers oprettes en relation med amount = 1.
    @Override
    public void incrementInventoryItem(Integer inventoryId, Integer itemId) {
        neo4jClient.query("""
                    MATCH (inventory:Inventory {id: $inventoryId})
                    MATCH (item:Item {id: $itemId})
                    MERGE (inventory)-[relation:CONTAINS]->(item)
                    ON CREATE SET relation.amount = 1
                    ON MATCH SET relation.amount = relation.amount + 1
                    """)
                .bind(inventoryId).to("inventoryId")
                .bind(itemId).to("itemId")
                .run();
    }

    // Finder characteren og itemet der skal equips.
    // Fjerner eksisterende relation i den relevante slot og opretter derefter den nye.
    @Override
    public void setEquippedItem(Integer characterId, Integer itemId, ItemType itemType) {
        EquipmentSlotRelation slotRelation = resolveEquipmentRelation(itemType);
        String relationshipType = slotRelation.relationshipType();

        String query = """
                MATCH (character:Character {id: $characterId})
                MATCH (item:Item {id: $itemId})
                OPTIONAL MATCH (character)-[oldRelation:%s]->(:Item)
                DELETE oldRelation
                CREATE (character)-[:%s]->(item)
                """.formatted(relationshipType, relationshipType);

        neo4jClient.query(query)
                .bind(characterId).to("characterId")
                .bind(itemId).to("itemId")
                .run();
    }

    // Fjerner equipped-relationen i den angivne slot for characteren.
    @Override
    public void removeEquippedItem(Integer characterId, ItemType itemType) {
        EquipmentSlotRelation slotRelation = resolveEquipmentRelation(itemType);
        String relationshipType = slotRelation.relationshipType();

        String query = """
                MATCH (character:Character {id: $characterId})-[relation:%s]->(:Item)
                DELETE relation
                """.formatted(relationshipType);

        neo4jClient.query(query)
                .bind(characterId).to("characterId")
                .run();
    }

    // Mapper intern item-data til response DTO for loadoutens equipped items.
    private ItemResponseDTO toItemResponseDTO(ItemData itemData) {
        return new ItemResponseDTO(
                itemData.itemId(),
                itemData.name(),
                itemData.description(),
                itemData.type()
        );
    }

    // Mapper intern inventory item-data til response DTO med amount og indlejret item.
    private InventoryHasItemResponseDTO toInventoryHasItemResponseDTO(InventoryItemData itemData) {
        return new InventoryHasItemResponseDTO(
                itemData.inventoryId(),
                itemData.amount(),
                new ItemResponseDTO(
                        itemData.itemId(),
                        itemData.name(),
                        itemData.description(),
                        itemData.type()
                )
        );
    }

    private enum EquipmentSlotRelation {
        CHEST("EQUIPPED_CHEST"),
        HEAD("EQUIPPED_HEAD"),
        LEGS("EQUIPPED_LEGS");

        private final String relationshipType;

        EquipmentSlotRelation(String relationshipType) {
            this.relationshipType = relationshipType;
        }

        public String relationshipType() {
            return relationshipType;
        }
    }

    private record InventoryItemData(
            Integer inventoryId,
            Integer itemId,
            String name,
            String description,
            ItemType type,
            Integer amount
    ) {}
}
