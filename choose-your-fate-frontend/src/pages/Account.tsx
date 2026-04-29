import { useAuth } from "../context/AuthContext";

export default function Account() {
  const { user, setUser } = useAuth();

  const logout = () => {
    setUser(null);
  };

  return (
    <div>
      <h1>Account</h1>
      <p>Welcome {user?.username}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
}