const ORDER_API = import.meta.env.VITE_ORDER_API_URL || 'http://order-gateway:8081';
const POSITION_API = import.meta.env.VITE_POSITION_API_URL || 'http://position-service:8083';


export async function placeOrder(order, token) {
    const res = await fetch(`${ORDER_API}/orders`, {
        method: 'POST',
        headers: { 
            'Content-Type' : 'application/json',
            Authorization: 'Bearer ${token}', 
        },
        body: JSON.stringify(order),
    });

    if(!res.ok) {
        const body = await res.json().catch(() => null);
        throw new Error(body?.message || `Order placement failed (${res.status})`);
    }

    return res.json();    
}

export async function fetchPositions(token) {
    const res = await fetch(`${POSITION_API}/api/positions`, {
        headers: {Authorization: `Bearer ${token}` },
    });
    if(!res.ok)
        throw new Error('Failed to load positions');
    return res.json();
}

export async function fetchRecentTrades(token) {
    const res = await fetch(`${POSITION_API}/api/trades`, {
        headers: {Authorization: `Bearer ${token}` },
    });
    if(!res.ok)
        throw new Error('Failed to load trades');
    return res.json();
}

export function orderWebSocketUrl(){
    return `${ORDER_API}/ws`;
}