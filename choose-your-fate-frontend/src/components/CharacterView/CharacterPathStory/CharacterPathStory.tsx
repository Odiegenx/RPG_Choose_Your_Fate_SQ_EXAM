import { useNavigate } from "react-router-dom";
import type { Props } from "../../../types/general";

import "./CharacterPathStory.css"

export default function CharacterPathStory({ character }: Props) {
      const navigate = useNavigate();


    const handleStartGame = async () => {
        try {
    
    
          navigate("/Game");
        } catch (err) {
          console.error(err);
          alert("Login failed");
        }
      };
    
    return (
        <div id="character-path-story" className="cw-container-row auto-width auto-height">
            <p>Story so far</p>
            <div id="paragraph">fdhsfkhsfkjhsfkhskfhsoeifhosgudfhlsghlsghsljgdljsggljsgkuæiedræghdægfhdarsækgdlghl</div>
            
            <button onClick={handleStartGame}>Play</button>
        </div>
    );
}