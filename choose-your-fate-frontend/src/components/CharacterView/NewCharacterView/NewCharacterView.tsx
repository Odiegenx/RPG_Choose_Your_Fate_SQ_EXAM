import { useEffect, useState } from "react";
import type { NewCharacterViewProps, Racedetails } from "../../../types/general";
import { apiGet, apiPost } from "../../../api/authApi";

import "./NewCharacterView.css";
import { useAuth } from "../../../context/AuthContext";
import React from "react";

export default function NewCharacterView({ character, onCharacterCreated }: NewCharacterViewProps) {
  const [raceDetails, setraceDetails] = useState<Racedetails[]>([]);
  const [name, setName] = React.useState("");
  const [loading, setLoading] = useState(true);
  const [selectedRaceDetailsId, setSelectedRaceDetailsId] = React.useState("");
  const [creating, setCreating] = React.useState(false);
  const { token } = useAuth();

  async function handleCreateCharacter() {
    if (!name.trim() || !selectedRaceDetailsId) {
      alert("Please enter a name and select a race");
      return;
    }

    try {
      setCreating(true);

      await apiPost(
        "characters",
        {
          raceDetailsId: Number(selectedRaceDetailsId),
          name: name.trim(),
        },
        { token: token }
      );

      setName("");
      setSelectedRaceDetailsId("");
      onCharacterCreated();
    } catch (err) {
      console.error(err);
      alert("Failed to create character");
    } finally {
      setCreating(false);
    }
  }

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);

        const [raceDetailsData] = await Promise.all([
          apiGet(`race-details`, {token: token}),
        ]);

        setraceDetails(raceDetailsData);

      } catch (err) {
        console.error(err);
        alert("Failed to load character details");
      } finally {
        setLoading(false);
      }
    }

    if (character?.id) {
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
        <div id="character-detail-grid" className="new-character-detail-grid-container-row">
            
            <input
                name="name"
                type="text"
                placeholder="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="max-height-40"
                autoFocus
            />

            <select
              id="RaceDetailsSelector"
              name="raceDetailsId"
              value={selectedRaceDetailsId}
              onChange={(e) => setSelectedRaceDetailsId(e.target.value)}
              className="max-height-40"
            >
              <option value="" disabled>
                Select race
              </option>

              {raceDetails.map((race) => (
                <option key={race.id} value={race.id}>
                  {race.name}
                </option>
              ))}
            </select>

            <button
              type="button"
              onClick={handleCreateCharacter}
              disabled={creating || !name.trim() || !selectedRaceDetailsId}
              className="max-height-40"
            >
              {creating ? "Creating..." : "Create character"}
            </button>
        </div>
    </div>
  );
}
