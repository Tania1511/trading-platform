import Header from "./components/Header";
import OrderForm from "./components/Orderform";
import PositionsTable from "./components/Positionstable";
import TradeBlotter from "./components/Tradeblotter";
import LoginForm from './components/LoginForm.jsx';
import {useAuth } from './useAuth.js';
import { useStompClient } from "./UseStompClient";


function App() {
  const { connected, subscribe } = useStompClient(token);
  const { token, login, logout, loading, error } = useAuth();

  if(!token) {
    return <LoginForm onLogin={login} loading={loading} error={error} />;
  }

  return (
    <div style={{ minHeight: '100vh'}} >
      <Header connected={connected} onLogout={logout} />
      <main style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <OrderForm token={token} />
        <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'minmax(0, 1.6fr) minmax(0, 1fr)',
              gap: '20px',
          }}
        >
          <TradeBlotter subscribe={subscribe} token={token} />
          <PositionsTable subscribe={subscribe} token={token} />
        </div>
      </main>
    </div>
  );
}

export default App
