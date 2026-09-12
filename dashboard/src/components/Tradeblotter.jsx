import { useEffect, useState } from "react";
import { fetchRecentTrades } from "../api";

export default function TradeBlotter ({ subscribe }) {
    
    const [trades, setTrades] = useState([]);
    const [flashId, setFlashId] = useState(null);

    useEffect(() => {
        fetchRecentTrades().then(setTrades).catch(() =>{});
    },[]);

    useEffect(() => {
        return subscribe('/topic/trades', (trade) => {
            setTrades((prev) => [trade, ...prev].slice(0,50));
            setFlashId(trade.tradeId);
            setTimeout(() => setFlashId(null),900);
        });
    },[subscribe]);

    return (
    <div style={{ background: 'var(--panel)', border: '1px solid var(--border)', borderRadius: '6px', overflow: 'hidden' }}>
      <div style={{ padding: '12px 16px', borderBottom: '1px solid var(--border)', fontSize: '13px', fontWeight: 600 }}>
        Trade blotter
      </div>
 
      <div style={{ maxHeight: '480px', overflowY: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ textAlign: 'left', color: 'var(--text-muted)' }}>
              <Th>Time</Th>
              <Th>Symbol</Th>
              <Th align="right">Price</Th>
              <Th align="right">Qty</Th>
              <Th>Buyer</Th>
              <Th>Seller</Th>
            </tr>
          </thead>
          <tbody>
            {trades.length === 0 && (
              <tr>
                <td colSpan={6} style={{ padding: '24px 16px', color: 'var(--text-muted)' }}>
                  No trades yet - place a crossing buy and sell order to see one here.
                </td>
              </tr>
            )}
            {trades.map((t) => (
              <tr
                key={t.tradeId}
                className={t.tradeId === flashId ? 'row-flash' : ''}
                style={{ borderTop: '1px solid var(--border)' }}
              >
                <td className="mono" style={cellStyle}>{formatTime(t.occurredAt)}</td>
                <td style={cellStyle}>{t.symbol}</td>
                <td className="mono" style={{ ...cellStyle, textAlign: 'right' }}>{Number(t.price).toFixed(2)}</td>
                <td className="mono" style={{ ...cellStyle, textAlign: 'right' }}>{Number(t.quantity).toFixed(0)}</td>
                <td className="mono buy-text" style={cellStyle}>{t.buyClientOrderId}</td>
                <td className="mono sell-text" style={cellStyle}>{t.sellClientOrderId}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
 
function Th({ children, align = 'left' }) {
  return (
    <th style={{ padding: '8px 16px', fontWeight: 500, textAlign: align, fontSize: '12px' }}>
      {children}
    </th>
  );
}
 
const cellStyle = { padding: '8px 16px' };
 
function formatTime(iso) {
  const d = new Date(iso);
  return d.toLocaleTimeString('en-US', { hour12: false });
}