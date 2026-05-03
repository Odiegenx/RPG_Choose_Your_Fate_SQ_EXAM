import { useEffect, useState } from "react";
//import { apiGet } from "../../../api/authApi";
import type { Character, CharacterListProps } from "../../../types/general";
import CharacterWindow from "./CharacterWindow/CharacterWindow";
import NewCharacterWindow from "./NewCharacterWindow/NewCharacterWindow";

import "./CharacterList.css"

export default function CharacterList({ onSelect }: CharacterListProps) {
  const [characters, setCharacters] = useState<Character[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchCharacters() {
      try {
        // const data: Character[] = await apiGet("characters");
        const data: Character[] = [
            {
                id: 1,
                accountId: 0,
                chapterId: 0,
                raceDetailsId: 0,
                sceneId: 0,
                name: "Dave the Bob"
            },{
                id: 2,
                accountId: 0,
                chapterId: 0,
                raceDetailsId: 0,
                sceneId: 0,
                name: "Johnny the Bob"
            },{
                id: 3,
                accountId: 0,
                chapterId: 0,
                raceDetailsId: 0,
                sceneId: 0,
                name: "Tommy the Bob"
            },{
                id: 4,
                accountId: 0,
                chapterId: 0,
                raceDetailsId: 0,
                sceneId: 0,
                name: "Gary the Bob"
            }
        ]

        setCharacters(data);
      } catch (err) {
        console.error(err);
        alert("Failed to load character");
      } finally {
        setLoading(false);
      }
    }

    fetchCharacters();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div id="character-list-view">
      {characters.map((character) => (
        <CharacterWindow key={character.id} character={character} onSelect={onSelect} />
      ))}
      <NewCharacterWindow />
    </div>
  );
}