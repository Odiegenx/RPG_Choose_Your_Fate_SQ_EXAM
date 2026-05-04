var btn = document.getElementById("tts_btn")

btn.onclick = ttsfunction

async function ttsfunction() {

    var text = document.getElementById("textInput").value;

    var url = "http://localhost:8080/choose-your-fate/tts/test"

    const response = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(text)
        }
    )

    const audioBlob = await response.blob();
    const audioUrl = URL.createObjectURL(audioBlob);
    const audio = document.getElementById("audioHolder");
    audio.src = audioUrl;
    audio.play();
}