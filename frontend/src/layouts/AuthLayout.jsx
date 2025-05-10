import React from 'react';
import { Outlet } from 'react-router-dom';
import styled from 'styled-components';
import { Link } from 'react-router-dom';

const AuthContainer = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--neutral-100);
`;

const AuthHeader = styled.header`
  padding: var(--spacing-4);
  background-color: var(--neutral-0);
  box-shadow: var(--shadow-sm);
`;

const Logo = styled(Link)`
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 500;
  color: var(--primary);
  text-decoration: none;
  
  &:hover {
    text-decoration: none;
  }
`;

const AuthMain = styled.main`
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-6);
`;

const AuthLayout = () => {
  return (
    <AuthContainer>
      <AuthHeader>
        <Logo to="/">
          <svg 
            width="24" 
            height="24" 
            viewBox="0 0 24 24" 
            fill="none" 
            xmlns="http://www.w3.org/2000/svg"
            style={{ marginRight: '8px' }}
          >
            <path 
              d="M12 2L2 7L12 12L22 7L12 2Z" 
              fill="#0052CC"
            />
            <path 
              d="M2 17L12 22L22 17" 
              stroke="#0052CC" 
              strokeWidth="2" 
              strokeLinecap="round" 
              strokeLinejoin="round"
            />
            <path 
              d="M2 12L12 17L22 12" 
              stroke="#0052CC" 
              strokeWidth="2" 
              strokeLinecap="round" 
              strokeLinejoin="round"
            />
          </svg>
          TaskFlow
        </Logo>
      </AuthHeader>
      
      <AuthMain>
        <Outlet />
      </AuthMain>
      
      <footer style={{ padding: 'var(--spacing-4)', textAlign: 'center', color: 'var(--neutral-600)' }}>
        © {new Date().getFullYear()} TaskFlow. All rights reserved.
      </footer>
    </AuthContainer>
  );
};

export default AuthLayout;