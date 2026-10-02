import React, { useState } from 'react'

const inputStyle = {
  background: 'var(--panel-raised)',
  border: '1px solid var(--border)',
  borderRadius: '4px',
  color: 'var(--text)',
  padding: '10px 12px',
  fontSize: '14px',
  width: '240px',
};

export default function LoginForm({ onLogin, loading, error}) {

    const [username, setUsername] = useState('trader1');
    const [password, setPassword] = useState('');


    function handleSubmit(e) {
        e.preventDefault();
        onLogin(username, password);
    }


  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh'}}>
        <form onSubmit={handleSubmit} style= {{ background: 'var(--panel)', border: '1px solid var(--border)', borderRadius: '8px', padding: '32px', display: 'flex', flexDirection: 'column', gap: '14px', width: '300px'}} >
            <h1 style={{ margin: 0, fontSize: '16px', fontWeight: 600 }}>Tradin Platform</h1>
            <span style={{ color: 'var(--text-muted)', fontSize: '13px', marginTop: '-8px' }}>Sign in to continue</span>
        
            <input style={inputStyle} placeholder='Username' value={username} onChange={(e) => setUsername(e.target.value)} required/>

            <input style={inputStyle} type="password" placeholder='Passowrd' value={username} onChange={(e) => setPassword(e.target.value)} required />

            <button type='submit'  disabled={loading} style={{  background: 'var(--live)',color: '#0d1117', border: 'none',borderRadius: '4px',padding: '10px',fontWeight: 600,fontSize: '13px',cursor: loading ? 'default' : 'pointer',opacity: loading ? 0.6 : 1,}}>
                {loading ? 'Signing in...' : 'Sign in'}
            </button>

            {error && <span style={{ color: 'var(--sell)', fontSize: '13px' }} >{error}</span>}
        </form>
    </div>
  )
}
