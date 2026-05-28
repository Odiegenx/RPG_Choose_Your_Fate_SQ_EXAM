import { useEffect, useState } from "react";
import type { CharacterDetailViewProps, InventoryLoadoutItem, Item, Loadout } from "../../../types/general";
import { apiGet } from "../../../api/authApi";

import InventoryView from "./InventoryView/InventoryView";
import EquipmentView from "./EquipmentView/EquipmentView";

import "./CharacterDetailView.css";
import { useAuth } from "../../../context/AuthContext";

export default function CharacterDetailView({ character }: CharacterDetailViewProps) {
  const [inventory, setInventory] = useState<InventoryLoadoutItem[]>([]);
  const [equipment, setEquipment] = useState<(Item | null)[]>([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);

        const loadoutData: Loadout = await apiGet(`loadout/${character.characterId}`, {token: token});

        setInventory(loadoutData.itemsInInventory);
        setEquipment(loadoutData.equippedItems);

      } catch (err) {
        console.error(err);
        alert("Failed to load character details");
      } finally {
        setLoading(false);
      }
    }

    if (character?.characterId) {
      fetchData();
    }
  }, [character, token]);

  if (loading) {
    return <div>Loading character details...</div>;
  }

  return (
    <div
      id="vw-character-details"
      className="cw-container auto-width auto-height margin-top-bot-10 margin-left-5"
    >
        <div id="character-detail-grid" className="character-detail-grid-container-row">
            
            <div id="character-detail-grid-inventory-portrait" className="character-detail-grid-container-column">
                <div id="character-detail-grid-equip" className="border padding-right-20">
                    <EquipmentView equipment={equipment} />
                </div>
                <div id="character-detail-grid-portrait" className="border">
                    
                    <p>Name: {character.characterName}</p>
                    <br />
                    <p>Race: {character.raceName}</p>
                    <br />
                    <p>Current chapter: {character.chapterName}</p>
                    <br />
                    <p>Intelligence: {character.stats.intelligence}</p>
                    <br />
                    <p>Charisma: {character.stats.charisma}</p>
                    <br />
                    <p>Fashion: {character.stats.fashion}</p>
                    <br />
                </div>
            </div>

            <div id="character-detail-grid-inventory" className="border">
                <InventoryView inventory={inventory} />
            </div>
        </div>
    </div>
  );
}
