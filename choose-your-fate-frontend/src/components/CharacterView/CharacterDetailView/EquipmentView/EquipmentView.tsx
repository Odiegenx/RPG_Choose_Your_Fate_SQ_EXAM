import type { Item } from "../../../../types/general";

export default function EquipmentView({ equipment }: { equipment: (Item | null)[] }) {
  return (
    <div>
      <h3>Equipment</h3>
      {equipment.filter((item) => item !== null).map((item) => (
        <div key={item.id} title={item.description}>
          {item.name}
        </div>
      ))}
    </div>
  );
}
