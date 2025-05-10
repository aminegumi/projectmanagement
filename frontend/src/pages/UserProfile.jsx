import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { useAuth } from '../context/AuthContext';

const PageContainer = styled.div`
  max-width: 800px;
  margin: 0 auto;
`;

const PageTitle = styled.h1`
  font-size: 24px;
  font-weight: 500;
  margin-bottom: var(--spacing-6);
  color: var(--neutral-900);
`;

const FormContainer = styled.div`
  background-color: var(--neutral-0);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-6);
`;

const FormGroup = styled.div`
  margin-bottom: var(--spacing-4);
`;

const Label = styled.label`
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: var(--spacing-2);
  color: var(--neutral-800);
`;

const Input = styled.input`
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  
  &:focus {
    outline: none;
    border-color: var(--primary-light);
    box-shadow: 0 0 0 2px var(--primary-lightest);
  }
`;

const AvatarSection = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-6);
`;

const Avatar = styled.div`
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: var(--primary-lightest);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 500;
  margin-right: var(--spacing-4);
`;

const AvatarActions = styled.div`
  display: flex;
  flex-direction: column;
`;

const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-6);
`;

const Button = styled.button`
  padding: var(--spacing-2) var(--spacing-6);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  
  &:not(:last-child) {
    margin-right: var(--spacing-3);
  }
`;

const SaveButton = styled(Button)`
  background-color: var(--primary);
  color: white;
  border: none;
  
  &:hover {
    background-color: var(--primary-dark);
  }
  
  &:disabled {
    background-color: var(--neutral-400);
    cursor: not-allowed;
  }
`;

const CancelButton = styled(Button)`
  background-color: var(--neutral-0);
  color: var(--neutral-700);
  border: 1px solid var(--neutral-300);
  
  &:hover {
    background-color: var(--neutral-100);
  }
`;

const FileInput = styled.input`
  display: none;
`;

const FileLabel = styled.label`
  display: inline-block;
  padding: var(--spacing-2) var(--spacing-4);
  background-color: var(--neutral-0);
  color: var(--neutral-700);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: var(--spacing-2);
  
  &:hover {
    background-color: var(--neutral-100);
  }
`;

const RemoveButton = styled.button`
  background: none;
  border: none;
  color: var(--neutral-600);
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline;
  padding: 0;
  
  &:hover {
    color: var(--error);
  }
`;

const ErrorMessage = styled.div`
  color: var(--error);
  font-size: 14px;
  margin-top: var(--spacing-2);
`;

const SuccessMessage = styled.div`
  color: var(--success);
  font-size: 14px;
  margin-top: var(--spacing-2);
`;

const PasswordSection = styled.div`
  margin-top: var(--spacing-8);
  padding-top: var(--spacing-6);
  border-top: 1px solid var(--neutral-200);
`;

const PasswordTitle = styled.h2`
  font-size: 18px;
  font-weight: 500;
  margin-bottom: var(--spacing-4);
  color: var(--neutral-900);
`;

const UserProfile = () => {
  const { currentUser } = useAuth();
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    role: ''
  });
  
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  useEffect(() => {
    if (currentUser) {
      setFormData({
        name: currentUser.name || '',
        email: currentUser.email || '',
        role: currentUser.role || ''
      });
    }
  }, [currentUser]);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };
  
  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData({
      ...passwordData,
      [name]: value
    });
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    setIsSubmitting(true);
    
    try {
      // In a real app, we would call an API here
      
      setSuccess('Profile updated successfully');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      console.error('Error updating profile:', err);
      setError('Failed to update profile');
    } finally {
      setIsSubmitting(false);
    }
  };
  
  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    
    // Basic validation
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setError('New passwords do not match');
      return;
    }
    
    if (passwordData.newPassword.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      // In a real app, we would call an API here
      
      setSuccess('Password updated successfully');
      setTimeout(() => setSuccess(null), 3000);
      
      // Clear password fields
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
    } catch (err) {
      console.error('Error updating password:', err);
      setError('Failed to update password');
    } finally {
      setIsSubmitting(false);
    }
  };
  
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };

  return (
    <PageContainer>
      <PageTitle>My Profile</PageTitle>
      
      <FormContainer>
        <AvatarSection>
          <Avatar>{getInitials(formData.name)}</Avatar>
          <AvatarActions>
            <FileLabel htmlFor="avatar-upload">
              Upload Photo
            </FileLabel>
            <FileInput
              id="avatar-upload"
              type="file"
              accept="image/*"
            />
            <RemoveButton>
              Remove Photo
            </RemoveButton>
          </AvatarActions>
        </AvatarSection>
        
        <form onSubmit={handleSubmit}>
          <FormGroup>
            <Label htmlFor="name">Full Name</Label>
            <Input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter your full name"
            />
          </FormGroup>
          
          <FormGroup>
            <Label htmlFor="email">Email</Label>
            <Input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email"
              disabled // Email cannot be changed
            />
          </FormGroup>
          
          <FormGroup>
            <Label htmlFor="role">Role</Label>
            <Input
              type="text"
              id="role"
              name="role"
              value={formData.role}
              disabled // Role is display-only
            />
          </FormGroup>
          
          {error && <ErrorMessage>{error}</ErrorMessage>}
          {success && <SuccessMessage>{success}</SuccessMessage>}
          
          <ButtonGroup>
            <CancelButton type="button" onClick={() => navigate('/dashboard')}>
              Cancel
            </CancelButton>
            <SaveButton type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Saving...' : 'Save Changes'}
            </SaveButton>
          </ButtonGroup>
        </form>
        
        <PasswordSection>
          <PasswordTitle>Change Password</PasswordTitle>
          
          <form onSubmit={handlePasswordSubmit}>
            <FormGroup>
              <Label htmlFor="currentPassword">Current Password</Label>
              <Input
                type="password"
                id="currentPassword"
                name="currentPassword"
                value={passwordData.currentPassword}
                onChange={handlePasswordChange}
                placeholder="Enter your current password"
              />
            </FormGroup>
            
            <FormGroup>
              <Label htmlFor="newPassword">New Password</Label>
              <Input
                type="password"
                id="newPassword"
                name="newPassword"
                value={passwordData.newPassword}
                onChange={handlePasswordChange}
                placeholder="Enter your new password"
              />
            </FormGroup>
            
            <FormGroup>
              <Label htmlFor="confirmPassword">Confirm New Password</Label>
              <Input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={passwordData.confirmPassword}
                onChange={handlePasswordChange}
                placeholder="Confirm your new password"
              />
            </FormGroup>
            
            <ButtonGroup>
              <SaveButton type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Updating...' : 'Update Password'}
              </SaveButton>
            </ButtonGroup>
          </form>
        </PasswordSection>
      </FormContainer>
    </PageContainer>
  );
};

export default UserProfile;