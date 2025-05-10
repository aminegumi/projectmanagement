import React, { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Sidebar from '../components/Sidebar';
import Header from '../components/Header';
import styled from 'styled-components';

const LayoutContainer = styled.div`
  display: flex;
  min-height: 100vh;
`;

const MainContent = styled.main`
  flex: 1;
  padding: var(--spacing-4);
  background-color: var(--neutral-100);
  margin-left: 260px;
  
  @media (max-width: 768px) {
    margin-left: 0;
    padding: var(--spacing-2);
  }
`;

const DashboardLayout = () => {
  const { currentUser, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <LayoutContainer>
      <Sidebar 
        isOpen={sidebarOpen} 
        onToggle={toggleSidebar} 
        user={currentUser}
      />
      
      <MainContent>
        <Header 
          user={currentUser} 
          onLogout={handleLogout}
          toggleSidebar={toggleSidebar}
        />
        
        <div className="dashboard-content">
          <Outlet />
        </div>
      </MainContent>
    </LayoutContainer>
  );
};

export default DashboardLayout;