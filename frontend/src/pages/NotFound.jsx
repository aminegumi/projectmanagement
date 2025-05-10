import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: var(--spacing-6);
  text-align: center;
`;

const ErrorCode = styled.div`
  font-size: 80px;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: var(--spacing-4);
`;

const Title = styled.h1`
  font-size: 24px;
  font-weight: 500;
  margin-bottom: var(--spacing-4);
  color: var(--neutral-900);
`;

const Description = styled.p`
  font-size: 16px;
  color: var(--neutral-600);
  max-width: 500px;
  margin-bottom: var(--spacing-6);
`;

const Button = styled(Link)`
  display: inline-flex;
  align-items: center;
  background-color: var(--primary);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-3) var(--spacing-6);
  font-weight: 500;
  text-decoration: none;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--primary-dark);
    text-decoration: none;
  }
`;

const NotFound = () => {
  return (
    <Container>
      <ErrorCode>404</ErrorCode>
      <Title>Page Not Found</Title>
      <Description>
        The page you are looking for might have been removed, had its name changed,
        or is temporarily unavailable.
      </Description>
      <Button to="/dashboard">Back to Dashboard</Button>
    </Container>
  );
};

export default NotFound;