import Header from "./components/Header";
import OrderForm from "./components/Orderform";
import PositionsTable from "./components/Positionstable";
import TradeBlotter from "./components/Tradeblotter";
import { useStompClient } from "./UseStompClient";


function App() {
  const { connected, subscribe } = useStompClient();

  return (
    <div style={{ minHeight: '100vh'}} >
      <Header connected={connected} />
      <main style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <OrderForm/>
        <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'minmax(0, 1.6fr) minmax(0, 1fr)',
              gap: '20px',
          }}
        >
          <TradeBlotter subscribe={subscribe} />
          <PositionsTable subscribe={subscribe} />
        </div>
      </main>
    </div>
  );
}

export default App
