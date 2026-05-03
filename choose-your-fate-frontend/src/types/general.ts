export type User = {
  username: string;
};

export type Scene = {
  id: number;
  dialog: string[];
  img: string;
  choices: Choice[];
};

export type Choice = {
    id: number
    name: string
    destination_id: number    
};

export type Character = {
  id: number;
  accountId: number;
  chapterId: number;
  sceneId: number;
  raceDetailsId: number;
  name: string;
};

export type Props = {
  character: Character;
};

export type CharacterWindowProps = {
  character: Character;
  onSelect: (character: Character) => void;
};

export type CharacterListProps = {
  onSelect: (character: Character) => void;
}

export type InventoryItem = {
  id: number;
  name: string;
};

export type EquipmentItem = {
  id: number;
  name: string;
};