import { useState } from "react";
import type { Character, CharacterView as CharacterViewData, SelectedCharacter } from "../../types/general";
import CharacterList from "./CharacterList/CharacterList";
import CharacterDetailView from "./CharacterDetailView/CharacterDetailView";
import NewCharacterView from "./NewCharacterView/NewCharacterView";
import CharacterPathStory from "./CharacterPathStory/CharacterPathStory";

import "./CharacterView.css"

export default function CharacterView() {
    const [selectedCharacter, setSelectedCharacter] = useState<SelectedCharacter | null>(null);
    const [characterListRefreshKey, setCharacterListRefreshKey] = useState(0);

    function handleCharacterCreated() {
        setSelectedCharacter(null);
        setCharacterListRefreshKey((currentKey) => currentKey + 1);
    }

    function isNewCharacter(character: SelectedCharacter): character is Character {
        return "id" in character && character.id == "new-char";
    }

    function isCharacterView(character: SelectedCharacter): character is CharacterViewData {
        return "characterId" in character;
    }
    
    return (
        <div id="vw-character" className="cw-container auto-width auto-height">
            <CharacterList onSelect={setSelectedCharacter} refreshKey={characterListRefreshKey} />
            {selectedCharacter && isCharacterView(selectedCharacter) && (
                <CharacterDetailView character={selectedCharacter} />
            )}
            {selectedCharacter && isNewCharacter(selectedCharacter) && (
                <NewCharacterView character={selectedCharacter} onCharacterCreated={handleCharacterCreated} />
            )}
            {selectedCharacter && isCharacterView(selectedCharacter) && (
                <CharacterPathStory character={selectedCharacter}/>
            )}
        </div>
    );
}
