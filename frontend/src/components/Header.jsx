import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

const HeaderContainer = styled.header`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4);
  background-color: var(--neutral-0);
  border-bottom: 1px solid var(--neutral-200);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
`;

const LeftSection = styled.div`
  display: flex;
  align-items: center;
`;

const RightSection = styled.div`
  display: flex;
  align-items: center;
`;

const MobileMenuButton = styled.button`
  display: none;
  background: none;
  border: none;
  color: var(--neutral-800);
  font-size: 20px;
  cursor: pointer;
  margin-right: var(--spacing-4);
  
  @media (max-width: 768px) {
    display: block;
  }
`;

const SearchBar = styled.div`
  position: relative;
  margin-left: var(--spacing-4);
  
  @media (max-width: 768px) {
    display: none;
  }
`;

const SearchInput = styled.input`
  padding: var(--spacing-2) var(--spacing-2) var(--spacing-2) var(--spacing-8);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  width: 300px;
  background-color: var(--neutral-100);
  
  &:focus {
    outline: none;
    border-color: var(--primary-light);
    background-color: var(--neutral-0);
  }
`;

const SearchIcon = styled.span`
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--neutral-600);
`;

const CreateButton = styled.button`
  display: flex;
  align-items: center;
  background-color: var(--primary);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-2) var(--spacing-4);
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--primary-dark);
  }
`;

const UserProfile = styled.div`
  display: flex;
  align-items: center;
  margin-left: var(--spacing-4);
  position: relative;
`;

const Avatar = styled.div`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--primary-lightest);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  cursor: pointer;
`;

const UserMenu = styled.div`
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: var(--spacing-2);
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-lg);
  width: 200px;
  z-index: var(--z-dropdown);
  overflow: hidden;
  transition: all 0.2s;
  opacity: ${props => props.isOpen ? 1 : 0};
  visibility: ${props => props.isOpen ? 'visible' : 'hidden'};
  transform: ${props => props.isOpen ? 'translateY(0)' : 'translateY(-10px)'};
`;

const UserMenuItem = styled.div`
  padding: var(--spacing-3) var(--spacing-4);
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--neutral-100);
  }
  
  &.logout {
    border-top: 1px solid var(--neutral-200);
    color: var(--error);
  }
`;

const Header = ({ user, onLogout, toggleSidebar }) => {
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  
  const toggleUserMenu = () => {
    setUserMenuOpen(!userMenuOpen);
  };
  
  const handleClickOutside = () => {
    if (userMenuOpen) setUserMenuOpen(false);
  };
  
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };

  return (
    <HeaderContainer>
      <LeftSection>
        <MobileMenuButton onClick={toggleSidebar}>
          ☰
        </MobileMenuButton>
        
        <SearchBar>
          <SearchIcon>🔍</SearchIcon>
          <SearchInput 
            type="text" 
            placeholder="Search tasks, projects, or people" 
          />
        </SearchBar>
      </LeftSection>
      
      <RightSection>
        <Link to="/projects/create">
          <CreateButton>
            + Create
          </CreateButton>
        </Link>
        
        <UserProfile>
          <Avatar onClick={toggleUserMenu}>
            {getInitials(user?.name)}
          </Avatar>
          
          <UserMenu isOpen={userMenuOpen}>
            <UserMenuItem>
              <Link to="/profile" style={{ display: 'block', color: 'inherit', textDecoration: 'none' }}>
                Profile
              </Link>
            </UserMenuItem>
            <UserMenuItem>
              <Link to="/settings" style={{ display: 'block', color: 'inherit', textDecoration: 'none' }}>
                Settings
              </Link>
            </UserMenuItem>
            <UserMenuItem className="logout" onClick={onLogout}>
              Log out
            </UserMenuItem>
          </UserMenu>
        </UserProfile>
      </RightSection>
    </HeaderContainer>
  );
};

export default Header;