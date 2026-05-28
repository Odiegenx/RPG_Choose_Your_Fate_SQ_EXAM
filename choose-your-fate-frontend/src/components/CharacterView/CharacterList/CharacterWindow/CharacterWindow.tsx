import type { CharacterWindowProps } from "../../../../types/general";
import "./CharacterWindow.css"


export default function CharacterWindow({ character, onSelect }: CharacterWindowProps) {

  const handleOpenCharacterView = async () => {
    onSelect(character);
  };

  return (
    <div id="character-window" onClick={handleOpenCharacterView}>
      <div className="character-info">
        <p>{character?.characterName ?? "Name"}</p>
        <p>{character?.chapterName ?? "Chapter"}</p>
        <p>{character?.raceName ?? "Race"}</p>
      </div>

      <div>
        <i className="arrow right"></i>
      </div>
    </div>
  );
}
