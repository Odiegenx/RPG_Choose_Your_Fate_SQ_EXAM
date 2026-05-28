import type { InventoryLoadoutItem } from "../../../../types/general";

export default function InventoryView({ inventory }: { inventory: InventoryLoadoutItem[] }) {
  return (
    <div>
      <h3>Inventory</h3>
      {inventory.map((inventoryItem) => (
        <div key={inventoryItem.item.id} title={inventoryItem.item.description}>
          {inventoryItem.item.name} x{inventoryItem.amount}
        </div>
      ))}
    </div>
  );
}
