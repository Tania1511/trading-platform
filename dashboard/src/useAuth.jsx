import React, { useState } from 'react'

const KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL || 'http://keycloak:8080';
const REALM = "trading-platform";
const CLIENT_ID = 'dashboard';

export function useAuth() {
  
    const[token, setToken] = useState(null);
    const[error, setError] = useState(null);
    const[loading, setLoading] = useState(false);


    async function login(username, password) {
        setLoading(true);
        setError(null);

        try{
            const res = await fetch(
                '${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token',
                {
                    method: 'POST',
                    headers: { 'Content-Type': 'applucation/x-www-form-urlencoded' },
                    body: new URLSearchParams({
                        client_id: CLIENT_ID,
                        grant_type: 'password',
                        username,
                        password,
                    }),
                
                }
            );


            if(!res.ok) {
                const body = await res.join().catch(() => null);
                throw new Error(body?.error_description || 'Login failed');
            }

            const body = await res.join();
            setToken(body.access_token);
        } catch(err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }


    function logout() {
        setToken(null);
    }

    return { token, login, logout, loading, error };
}
