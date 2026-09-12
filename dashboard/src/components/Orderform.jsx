import { useState } from "react";
import { placeOrder } from '../api.js';

const panelStyle = {
    background: 'var(--panel)',
    border: '1px solid var(--border)',
    borderRadius: '6px',
    padding: '16px 20px',
};


const inputStyle = {
    background: 'var(--panel-raised)',
    border: '1px solid var(--border)',
    borderRadius: '4px',
    color: 'var(--text)',
    padding: '8px 10px',
    fontSize: '13px',
    fontFamily: 'var(--font-data)',
};


export default function OrderForm() {

    const[form, setForm] = useState({ symbol: 'AAPL', side: 'BUY', price: '', quantity: ''});
    const[status, setStatus] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    function update(field, value) {
        setForm((prev) => ({ 
            ...prev, 
            [field]: value
        }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setSubmitting(true);
        setStatus(null);

        const clientOrderId = crypto.randomUUID();

        try {
            const result = await placeOrder({
                clientOrderId,
                symbol: form.symbol,
                side: form.side,
                price: Number(form.price),
                quantity: Number(form.quantity),
            });

            setStatus({ type: 'success', message: `Order placed - status: ${result.status}` });
            setForm((prev) => ({ 
                ...prev,
                price: '',
                quantity: '' 
            }));

        }catch(err) {
            setStatus ({ type: 'error', message: err.message });
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div style={panelStyle}>
            <form onSubmit={handleSubmit} style={{ display: 'flex', alignItems: 'flex-end', gap: '12px', flexWrap: 'wrap' }}>
                <Field label="Symbol">
                    <input
                        style={{ ...inputStyle, width: '90px'}}
                        value={form.symbol}
                        onChange={(e) => update('symbol', e.target.value.toUpperCase())}
                        required
                    />
                </Field>

                <Field label ="Side">
                    <select 
                        style={{ ...inputStyle, width: '90px'}}
                        value={form.side}
                        onChange={(e) => update('side', e.target.value)}
                    >
                        <option value="BUY">BUY</option>
                        <option value='SELL'>SELL</option>
                    </select>
                </Field>

                <Field label="Price">
                    <input
                        style={{ ...inputStyle, width: '110px' }}
                        type="number"
                        step="0.01"
                        min="0.01"
                        value={form.price}
                        onChange={(e) => update('price',e.target.value)}
                        required
                    />
                </Field>

                <Field label="Quantity">
                    <input
                        style={{ ...inputStyle, width: '90px' }}
                        type="number"
                        step="1"
                        min="1"
                        value={form.quantity}
                        onChange={(e) => update('quantity',e.target.value)}
                        required
                    />
                </Field>

                <button
                    type="submit"
                    disabled={submitting}
                    style={{
                        background: form.side === 'BUY' ? 'var(--buy)' : 'var(--sell)',
                        color: '#0d1117',
                        border: 'none',
                        borderRadius: '4px',
                        padding: '9px 20px',
                        fontWeight: 600,
                        fontSize: '13px',
                        cursor: submitting ? 'default' : 'pointer',
                        opacity: submitting ? 0.6 : 1,
                    }}
                >
                    {submitting ? 'Placing...' : `Place ${form.side === 'BUY' ? 'buy' : 'sell'} order`}
                </button>

                {
                    status && (
                        <span
                            role="status"
                            style={{
                                fontSize: '13px',
                                color: status.type === 'error' ? 'var(--sell)' : 'var(--buy)',
                            }}
                        >
                            {status.message}
                        </span>
                    )
                }
            </form>
        </div>
    )
}

function Field ({ label, children }) {
    return (
        <label style={{ display: 'flex', flexDirection: 'column', gap: '5px'}}>
            <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{label}</span>
            {children}
        </label>
    );
}