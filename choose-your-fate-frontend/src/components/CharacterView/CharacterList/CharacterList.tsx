import { useEffect, useState } from "react";
import { apiGet } from "../../../api/authApi";
import type { CharacterView, CharacterListProps, CharacterViewResponse } from "../../../types/general";
import CharacterWindow from "./CharacterWindow/CharacterWindow";
import NewCharacterWindow from "./NewCharacterWindow/NewCharacterWindow";

import "./CharacterList.css"
import { useAuth } from "../../../context/AuthContext";

export default function CharacterList({ onSelect, refreshKey }: CharacterListProps) {
  const [characters, setCharacters] = useState<CharacterView[]>([]);
  const [canCreateMoreCharacters, setCanCreateMoreCharacters] = useState(false);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();

  useEffect(() => {
    async function fetchCharacters() {
      try {
        const data: CharacterViewResponse = await apiGet(`characters/all/view`, {token: token});
        

        setCharacters(data.views);
        setCanCreateMoreCharacters(data.canCreateMoreCharacters);
      } catch (err) {
        console.error(err);
        alert("Failed to load character");
      } finally {
        setLoading(false);
      }
    }

    fetchCharacters();
  }, [token, refreshKey]);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div id="character-list-view">
      {characters.map((character) => (
        <CharacterWindow key={character.characterId} character={character} onSelect={onSelect} />
      ))}
      {canCreateMoreCharacters && (
        <NewCharacterWindow onSelect={onSelect} />
      )}
    </div>
  );
}
