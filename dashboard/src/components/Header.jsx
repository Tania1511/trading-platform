export default function Header ({ connected }) {
    return (
        <header
            style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '16px 24px',
                borderBottom: '1px solid var(--border)',
            }}
        >

            <div style= {{ display: 'flex', alignItems: 'baseline', gap: '10px'}}>
                <h1 style={{ margin: 0, fontSize: '17px', fontWeight: 600}}>
                    Trading Platform
                </h1>
                <span style={{ color: 'var(--text-muted)', fontSize: '13px'}}>
                    Order Execution &amp; Position Monitor
                </span>
            </div>
            
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px'}}>
                <span 
                    aria-hidden="true"
                    style={{
                        width: '8px',
                        height: '8px',
                        borderRadius: '50%',
                        background: connected ? 'var(--buy)' : 'var(--sell)',
                        display: 'inline-block',
                    }}
                />
                <span style={{ color: 'var(--text-muted)' }}>
                    {connected ? 'Live' : 'Reconnecting...'}
                </span>
            </div>

        </header>   
    )
}