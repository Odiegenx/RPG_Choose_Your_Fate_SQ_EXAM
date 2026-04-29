import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const { setUser } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async () => {
    // Fake login for now
    setUser({ username: "test" });
    navigate("/account");
  };

  return (
    <div>
      <h1>Login</h1>
      <button onClick={handleLogin}>Login</button>
      <button onClick={() => navigate("/register")}>Go to Register</button>
    </div>
  );
}