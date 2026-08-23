import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';

interface AuthContextType {
    token: String | null;
    email: String | null;
    userId: String | null;
    isAuthenticated: boolean;
    login: (token: string, email: string, userId: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
    const [email, setEmail] = useState<string | null>(localStorage.getItem('email'));
    const [userId, setUserId] = useState<string | null>(localStorage.getItem('userId'));

    useEffect(() => {
        if (token) {
            localStorage.setItem('token', token);
            if (email) localStorage.setItem('email', email);
            if (userId) localStorage.setItem('userId', userId);
        } else {
            localStorage.removeItem('token');
            localStorage.removeItem('email');
            localStorage.removeItem('userId');
        }
    }, [token, email, userId]);

    const login = (newToken: string, newEmail: string, newUserId: string) => {
        setToken(newToken);
        setEmail(newEmail);
        setUserId(newUserId);
    };

    const logout = () => {
        setToken(null);
        setEmail(null);
        setUserId(null);
    };

    return (
        <AuthContext.Provider value={{ token, email, userId, isAuthenticated: !!token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
