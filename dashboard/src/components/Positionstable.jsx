import { useEffect, useState } from "react";
import { fetchPositions } from "../api";

export default function PositionsTable({ subscribe }) {
    const [positions, setPositions] = useState([]);
    const [flashKey, setFlashKey] = useState(null);

    useEffect(() => {
        fetchPositions().then(setPositions).catch(() => {});
    },[]);

    useEffect(() => {
        return subscribe('/topic/positions', (update) => {
            const key = `${update.accountKey}:${update.symbol}`;

            setPositions((prev) => {
                const existingIndex = prev.findIndex((p) => `${p.accountKey}:${p.symbol}` === key);
                if(existingIndex === -1) 
                    return [...prev, update];
                const next = [...prev];
                next[existingIndex] = update;
                return next;
            });

            setFlashKey(key);
            setTimeout(() => setFlashKey(null), 900);
        });
    },[subscribe]);

    return (
         <div style={{ background: 'var(--panel)', border: '1px solid var(--border)', borderRadius: '6px', overflow: 'hidden' }}>
      <div style={{ padding: '12px 16px', borderBottom: '1px solid var(--border)', fontSize: '13px', fontWeight: 600 }}>
        Positions &amp; P&amp;L
      </div>
 
      <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
        <thead>
          <tr style={{ textAlign: 'left', color: 'var(--text-muted)' }}>
            <Th>Account</Th>
            <Th>Symbol</Th>
            <Th align="right">Qty</Th>
            <Th align="right">Avg cost</Th>
            <Th align="right">Realized P&amp;L</Th>
          </tr>
        </thead>
        <tbody>
            {positions.length === 0 && (
            <tr>
              <td colSpan={5} style={{ padding: '24px 16px', color: 'var(--text-muted)' }}>
                No positions yet.
              </td>
            </tr>
          )}
          {positions.map((p) => {
            const key = `${p.accountKey}:${p.symbol}`;
            const qty = Number(p.quantity);
            const pnl = Number(p.realizedPnL);
            return (
              <tr
                key={key}
                className={key === flashKey ? 'row-flash' : ''}
                style={{ borderTop: '1px solid var(--border)' }}
              >
                <td className="mono" style={cellStyle}>{p.accountKey}</td>
                <td style={cellStyle}>{p.symbol}</td>
                <td
                  className={`mono ${qty > 0 ? 'buy-text' : qty < 0 ? 'sell-text' : ''}`}
                  style={{ ...cellStyle, textAlign: 'right' }}
                >
                  {qty > 0 ? '+' : ''}{qty.toFixed(0)}
                </td>
                <td className="mono" style={{ ...cellStyle, textAlign: 'right' }}>
                  {Number(p.averageCost).toFixed(2)}
                </td>
                <td
                  className={`mono ${pnl > 0 ? 'buy-text' : pnl < 0 ? 'sell-text' : ''}`}
                  style={{ ...cellStyle, textAlign: 'right' }}
                >
                  {pnl > 0 ? '+' : ''}{pnl.toFixed(2)}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
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
    