import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import ReactMarkdown from 'react-markdown';
import { reportAPI } from '../config/api';
import { toast } from 'react-toastify';

const PageContainer = styled.div`
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--spacing-4);
`;

const PageHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
`;

const BackButton = styled.button`
  display: flex;
  align-items: center;
  padding: var(--spacing-2) var(--spacing-4);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--primary);
  font-weight: 500;
  
  &:hover {
    text-decoration: underline;
  }
`;

const ReportContainer = styled.div`
  background-color: white;
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--neutral-200);
  padding: var(--spacing-6);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
`;

const ReportHeader = styled.div`
  margin-bottom: var(--spacing-6);
`;

const ReportTitle = styled.h1`
  font-size: 28px;
  font-weight: 600;
  margin-bottom: var(--spacing-2);
  color: var(--neutral-900);
`;

const ReportMeta = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-4);
  color: var(--neutral-600);
  font-size: 14px;
  margin-bottom: var(--spacing-4);
`;

const ReportTag = styled.span`
  display: inline-block;
  padding: var(--spacing-1) var(--spacing-2);
  background-color: var(--primary-light);
  color: var(--primary-dark);
  font-size: 12px;
  font-weight: 500;
  border-radius: var(--border-radius-full);
`;

const ReportContent = styled.div`
  line-height: 1.6;
  
  h1, h2, h3, h4, h5, h6 {
    margin-top: var(--spacing-6);
    margin-bottom: var(--spacing-3);
    color: var(--neutral-900);
  }
  
  h1 {
    font-size: 24px;
  }
  
  h2 {
    font-size: 20px;
  }
  
  h3 {
    font-size: 18px;
  }
  
  p {
    margin-bottom: var(--spacing-4);
  }
  
  ul, ol {
    margin-bottom: var(--spacing-4);
    padding-left: var(--spacing-5);
  }
  
  li {
    margin-bottom: var(--spacing-2);
  }
  
  code {
    background-color: var(--neutral-100);
    padding: 2px 4px;
    border-radius: 4px;
    font-family: monospace;
  }
  
  blockquote {
    border-left: 4px solid var(--primary-light);
    padding-left: var(--spacing-4);
    margin-left: 0;
    margin-right: 0;
    margin-bottom: var(--spacing-4);
    color: var(--neutral-700);
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: var(--spacing-4);
  }
  
  th, td {
    border: 1px solid var(--neutral-300);
    padding: var(--spacing-2) var(--spacing-3);
  }
  
  th {
    background-color: var(--neutral-100);
    font-weight: 500;
  }
`;

const Button = styled.button`
  padding: var(--spacing-2) var(--spacing-4);
  background-color: ${props => props.primary ? 'var(--primary)' : 'white'};
  color: ${props => props.primary ? 'white' : 'var(--neutral-800)'};
  border: 1px solid ${props => props.primary ? 'var(--primary)' : 'var(--neutral-300)'};
  border-radius: var(--border-radius-md);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background-color: ${props => props.primary ? 'var(--primary-dark)' : 'var(--neutral-100)'};
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: var(--spacing-3);
  margin-top: var(--spacing-6);
`;

const PromptContainer = styled.div`
  margin-top: var(--spacing-6);
  padding: var(--spacing-4);
  background-color: var(--neutral-50);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--neutral-200);
`;

const PromptLabel = styled.h3`
  font-size: 16px;
  font-weight: 500;
  margin-bottom: var(--spacing-2);
  color: var(--neutral-700);
`;

const ReportDetail = () => {
  const { reportId } = useParams();
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  
  useEffect(() => {
    const fetchReport = async () => {
      try {
        setLoading(true);
        const response = await reportAPI.getById(reportId);
        setReport(response.data);
      } catch (error) {
        console.error('Error fetching report:', error);
        toast.error('Could not fetch report details');
      } finally {
        setLoading(false);
      }
    };
    
    fetchReport();
  }, [reportId]);
  
  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this report?')) {
      try {
        await reportAPI.delete(reportId);
        toast.success('Report deleted successfully');
        navigate(-1);
      } catch (error) {
        console.error('Error deleting report:', error);
        toast.error('Could not delete report');
      }
    }
  };
  
  const handleExport = () => {
    const element = document.createElement('a');
    
    // Convert Markdown to HTML for better print formatting
    const content = `# ${report.title}\n\n${report.content}`;
    
    const blob = new Blob([content], { type: 'text/markdown' });
    element.href = URL.createObjectURL(blob);
    element.download = `${report.title.replace(/\s+/g, '-').toLowerCase()}.md`;
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  };
  
  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };
  
  if (loading) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: 'var(--spacing-10)' }}>
          Loading report...
        </div>
      </PageContainer>
    );
  }
  
  if (!report) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: 'var(--spacing-10)' }}>
          Report not found
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <BackButton onClick={() => navigate(-1)}>
          ← Back
        </BackButton>
      </PageHeader>
      
      <ReportContainer>
        <ReportHeader>
          <ReportTitle>{report.title}</ReportTitle>
          <ReportMeta>
            <div>Project: <strong>{report.projectName}</strong></div>
            <div>Created: <strong>{formatDate(report.createdAt)}</strong></div>
            <div>Author: <strong>{report.authorName}</strong></div>
            <ReportTag>{report.type.replace('_', ' ')}</ReportTag>
          </ReportMeta>
        </ReportHeader>
        
        <ReportContent>
          <ReactMarkdown>{report.content}</ReactMarkdown>
        </ReportContent>
        
        <PromptContainer>
          <PromptLabel>Generated from prompt:</PromptLabel>
          <div>{report.prompt}</div>
        </PromptContainer>
        
        <ButtonGroup>
          <Button onClick={handleDelete}>Delete Report</Button>
          <Button onClick={() => navigate(`/projects/${report.projectId}`)}>
            Go to Project
          </Button>
          <Button primary onClick={handleExport}>
            Export as Markdown
          </Button>
        </ButtonGroup>
      </ReportContainer>
    </PageContainer>
  );
};

export default ReportDetail;