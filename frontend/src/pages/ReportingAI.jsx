import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { projectAPI, reportAPI } from '../config/api';
import ReactMarkdown from 'react-markdown';
import { toast } from 'react-toastify';

const PageContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-4);
`;

const PageHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
`;

const PageTitle = styled.h1`
  font-size: 24px;
  font-weight: 500;
  color: var(--neutral-900);
`;

const ProjectSelector = styled.select`
  padding: var(--spacing-2) var(--spacing-3);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  background-color: white;
  min-width: 200px;
`;

const ChatContainer = styled.div`
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px);
  background-color: white;
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--neutral-200);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
`;

const MessagesContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-4);
`;

const MessageBubble = styled.div`
  max-width: 80%;
  padding: var(--spacing-3);
  margin-bottom: var(--spacing-3);
  border-radius: var(--border-radius-lg);
  
  ${props => props.isUser ? `
    background-color: var(--primary-light);
    color: var(--primary-dark);
    align-self: flex-end;
    margin-left: auto;
  ` : `
    background-color: var(--neutral-100);
    color: var(--neutral-800);
    align-self: flex-start;
    margin-right: auto;
  `}
`;

const MessageInput = styled.div`
  display: flex;
  padding: var(--spacing-3);
  border-top: 1px solid var(--neutral-200);
`;

const Input = styled.input`
  flex: 1;
  padding: var(--spacing-3);
  border: 1px solid var(--neutral-300);
  border-radius: var(--border-radius-md);
  margin-right: var(--spacing-2);
`;

const Button = styled.button`
  padding: var(--spacing-3) var(--spacing-5);
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
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

const ReportTypeSelector = styled.div`
  display: flex;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-4);
`;

const ReportTypeButton = styled.button`
  padding: var(--spacing-2) var(--spacing-3);
  background-color: ${props => props.selected ? 'var(--primary)' : 'white'};
  color: ${props => props.selected ? 'white' : 'var(--neutral-800)'};
  border: 1px solid ${props => props.selected ? 'var(--primary)' : 'var(--neutral-300)'};
  border-radius: var(--border-radius-md);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background-color: ${props => props.selected ? 'var(--primary-dark)' : 'var(--neutral-100)'};
  }
`;

const SuggestedPrompts = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-4);
`;

const PromptChip = styled.div`
  padding: var(--spacing-2) var(--spacing-3);
  background-color: var(--neutral-100);
  border-radius: var(--border-radius-full);
  font-size: 14px;
  cursor: pointer;
  
  &:hover {
    background-color: var(--neutral-200);
  }
`;

const SaveReportButton = styled(Button)`
  margin-left: var(--spacing-2);
`;

const Tabs = styled.div`
  display: flex;
  margin-bottom: var(--spacing-4);
  border-bottom: 1px solid var(--neutral-300);
`;

const Tab = styled.div`
  padding: var(--spacing-3) var(--spacing-5);
  font-weight: 500;
  cursor: pointer;
  border-bottom: 2px solid ${props => props.active ? 'var(--primary)' : 'transparent'};
  color: ${props => props.active ? 'var(--primary)' : 'var(--neutral-700)'};
  
  &:hover {
    color: var(--primary);
  }
`;

const SavedReportsContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-4);
`;

const ReportCard = styled.div`
  background-color: white;
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--neutral-200);
  padding: var(--spacing-4);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  
  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-color: var(--neutral-300);
  }
`;

const ReportCardTitle = styled.h3`
  font-size: 16px;
  font-weight: 500;
  margin-bottom: var(--spacing-2);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

const ReportCardMeta = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3);
  font-size: 14px;
  color: var(--neutral-600);
`;

const ReportCardActions = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-2);
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

const ReportPreview = styled.div`
  margin-top: var(--spacing-3);
  padding: var(--spacing-3);
  background-color: var(--neutral-50);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  color: var(--neutral-700);
  height: 80px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
`;

// Define report types
const REPORT_TYPES = [
  { id: 'STATUS_REPORT', label: 'Status Report' },
  { id: 'SPRINT_ANALYSIS', label: 'Sprint Analysis' },
  { id: 'TEAM_PERFORMANCE', label: 'Team Performance' },
  { id: 'RISK_ASSESSMENT', label: 'Risk Assessment' },
  { id: 'CUSTOM', label: 'Custom' }
];

// Sample suggested prompts
const SUGGESTED_PROMPTS = {
  'STATUS_REPORT': [
    "Generate a weekly status report for stakeholders",
    "Create a detailed progress summary",
    "Prepare an executive summary of project status"
  ],
  'SPRINT_ANALYSIS': [
    "Analyze our latest sprint performance",
    "Compare completed vs planned work in the sprint",
    "What can we improve for our next sprint?"
  ],
  'TEAM_PERFORMANCE': [
    "Evaluate team productivity and collaboration",
    "Identify areas where team efficiency can be improved",
    "Analyze individual contributions to the project"
  ],
  'RISK_ASSESSMENT': [
    "Identify potential risks based on current project status",
    "What are the biggest threats to our timeline?",
    "Suggest risk mitigation strategies"
  ],
  'CUSTOM': [
    "Generate a burndown chart analysis",
    "Compare estimated vs actual completion times",
    "Create a project health scorecard"
  ]
};

const ReportingAI = () => {
  const { projectId: urlProjectId } = useParams();
  const [activeTab, setActiveTab] = useState('generate');
  const [projects, setProjects] = useState([]);
  const [selectedProject, setSelectedProject] = useState(urlProjectId || '');
  const [projectData, setProjectData] = useState(null);
  const [reportType, setReportType] = useState('STATUS_REPORT');
  const [messages, setMessages] = useState([
    { 
      id: 1, 
      text: "Hello! I'm your AI reporting assistant. Select a project and report type, then ask me to generate reports, analyze data, or provide insights about your project.", 
      isUser: false 
    }
  ]);
  const [savedReports, setSavedReports] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const navigate = useNavigate();
  
  // Fetch all projects
  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await projectAPI.getAll();
        setProjects(response.data);
        
        // If we have a project ID from URL, set it as selected
        if (urlProjectId && !selectedProject) {
          setSelectedProject(urlProjectId);
        }
      } catch (error) {
        console.error('Error fetching projects:', error);
        toast.error('Could not fetch projects');
      }
    };
    
    fetchProjects();
  }, [urlProjectId]);
  
  // Fetch project data when a project is selected
  useEffect(() => {
    if (!selectedProject) return;
    
    const fetchProjectData = async () => {
      try {
        setLoading(true);
        
        // Fetch project details
        const projectResponse = await projectAPI.getById(selectedProject);
        setProjectData(projectResponse.data);
        
        // If we're on the saved reports tab, fetch them
        if (activeTab === 'saved') {
          fetchSavedReports();
        } else {
          // Add a message from the assistant about the selected project
          setMessages(prev => [...prev, {
            id: Date.now(),
            text: `I've loaded data for ${projectResponse.data.name}. Select a report type and ask a question to generate a report.`,
            isUser: false
          }]);
        }
      } catch (error) {
        console.error('Error fetching project data:', error);
        toast.error('Could not fetch project details');
      } finally {
        setLoading(false);
      }
    };
    
    fetchProjectData();
  }, [selectedProject, activeTab]);
  
  // Fetch saved reports for a project
  const fetchSavedReports = async () => {
    if (!selectedProject) return;
    
    try {
      setLoading(true);
      const response = await reportAPI.getByProject(selectedProject);
      setSavedReports(response.data);
    } catch (error) {
      console.error('Error fetching saved reports:', error);
      toast.error('Could not fetch saved reports');
    } finally {
      setLoading(false);
    }
  };
  
  // Scroll to bottom of messages when new ones are added
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);
  
  const handleProjectChange = (e) => {
    const newProjectId = e.target.value;
    setSelectedProject(newProjectId);
    
    // Update URL without reloading the page
    navigate(`/reporting/${newProjectId}`, { replace: true });
    
    // Reset messages when changing projects
    if (activeTab === 'generate') {
      setMessages([{ 
        id: Date.now(), 
        text: "Hello! I'm your AI reporting assistant. Select a project and report type, then ask me to generate reports, analyze data, or provide insights about your project.", 
        isUser: false 
      }]);
    }
  };
  
  const handleReportTypeChange = (type) => {
    setReportType(type);
    
    // Add a message about the selected report type
    setMessages(prev => [...prev, {
      id: Date.now(),
      text: `You've selected ${REPORT_TYPES.find(t => t.id === type).label}. What would you like to know?`,
      isUser: false
    }]);
  };
  
  // In ReportingAI.jsx
const handleSendMessage = async () => {
  if (!input.trim() || !selectedProject) return;
  
  // Add user message to chat
  const userMessage = { id: Date.now(), text: input, isUser: true };
  setMessages(prev => [...prev, userMessage]);
  
  // Clear input
  setInput('');
  
  // Show loading state
  setLoading(true);
  
  try {
      console.log('Generating report with:', {
          projectId: selectedProject,
          prompt: input,
          type: reportType
      });
      
      // Call API to generate report
      const response = await reportAPI.generate(selectedProject, input, reportType);
      console.log('API response:', response);
      
      if (response.data && response.data.content) {
          // Add the response to messages
          setMessages(prev => [...prev, {
              id: Date.now(),
              text: response.data.content,
              isUser: false,
              reportData: response.data
          }]);
      } else {
          throw new Error('Empty or invalid response from API');
      }
      
  } catch (error) {
      console.error('Error generating report:', error);
      
      // Add more detailed error message
      setMessages(prev => [...prev, {
          id: Date.now(),
          text: `I'm sorry, I encountered an error while generating your report: ${error.message || 'Unknown error'}. Please try again.`,
          isUser: false
      }]);
  } finally {
      setLoading(false);
  }
};
  
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };
  
  const handlePromptClick = (prompt) => {
    setInput(prompt);
  };
  
  const handleViewReport = (reportId) => {
    navigate(`/reports/${reportId}`);
  };
  
  const handleDeleteReport = async (reportId) => {
    if (window.confirm('Are you sure you want to delete this report?')) {
      try {
        await reportAPI.delete(reportId);
        toast.success('Report deleted successfully');
        fetchSavedReports();
      } catch (error) {
        console.error('Error deleting report:', error);
        toast.error('Could not delete report');
      }
    }
  };
  
  const handleSaveReport = async (message) => {
    // The report is already saved, this just confirms to the user
    toast.success('Report saved successfully');
  };
  
  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };
  
  const renderTabContent = () => {
    if (activeTab === 'generate') {
      return (
        <>
          <ReportTypeSelector>
            {REPORT_TYPES.map(type => (
              <ReportTypeButton
                key={type.id}
                selected={reportType === type.id}
                onClick={() => handleReportTypeChange(type.id)}
              >
                {type.label}
              </ReportTypeButton>
            ))}
          </ReportTypeSelector>
          
          <SuggestedPrompts>
            {SUGGESTED_PROMPTS[reportType]?.map((prompt, index) => (
              <PromptChip key={index} onClick={() => handlePromptClick(prompt)}>
                {prompt}
              </PromptChip>
            ))}
          </SuggestedPrompts>
          
          <ChatContainer>
            <MessagesContainer>
              {messages.map(message => (
                <MessageBubble key={message.id} isUser={message.isUser}>
                  {message.isUser ? (
                    message.text
                  ) : (
                    <>
                      <ReactMarkdown>{message.text}</ReactMarkdown>
                      {message.reportData && (
                        <Button 
                          style={{ marginTop: 'var(--spacing-2)' }}
                          onClick={() => handleSaveReport(message)}
                        >
                          Save Report
                        </Button>
                      )}
                    </>
                  )}
                </MessageBubble>
              ))}
              {loading && (
                <MessageBubble isUser={false}>
                  Generating report...
                </MessageBubble>
              )}
              <div ref={messagesEndRef} />
            </MessagesContainer>
            
            <MessageInput>
              <Input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Ask for project reports, analytics, or insights..."
                disabled={loading || !selectedProject}
              />
              <Button 
                primary 
                onClick={handleSendMessage}
                disabled={loading || !input.trim() || !selectedProject}
              >
                Send
              </Button>
            </MessageInput>
          </ChatContainer>
        </>
      );
    } else if (activeTab === 'saved') {
      return (
        <SavedReportsContainer>
          {loading ? (
            <div>Loading saved reports...</div>
          ) : savedReports.length === 0 ? (
            <div>No saved reports for this project yet. Generate some reports first!</div>
          ) : (
            savedReports.map(report => (
              <ReportCard key={report.id}>
                <ReportCardTitle>{report.title}</ReportCardTitle>
                <ReportCardMeta>
                  <ReportTag>{report.type.replace('_', ' ')}</ReportTag>
                  <span>{formatDate(report.createdAt)}</span>
                </ReportCardMeta>
                <ReportPreview>
                  {report.content.substring(0, 150)}...
                </ReportPreview>
                <ReportCardActions>
                  <Button onClick={() => handleDeleteReport(report.id)}>Delete</Button>
                  <Button primary onClick={() => handleViewReport(report.id)}>View</Button>
                </ReportCardActions>
              </ReportCard>
            ))
          )}
        </SavedReportsContainer>
      );
    }
  };

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>AI Project Reporting</PageTitle>
        <ProjectSelector 
          value={selectedProject} 
          onChange={handleProjectChange}
        >
          <option value="">Select a project</option>
          {projects.map(project => (
            <option key={project.id} value={project.id}>
              {project.name}
            </option>
          ))}
        </ProjectSelector>
      </PageHeader>
      
      {!selectedProject ? (
        <div style={{ textAlign: 'center', padding: 'var(--spacing-10)' }}>
          Please select a project to generate or view reports
        </div>
      ) : (
        <>
          <Tabs>
            <Tab 
              active={activeTab === 'generate'} 
              onClick={() => setActiveTab('generate')}
            >
              Generate Report
            </Tab>
            <Tab 
              active={activeTab === 'saved'} 
              onClick={() => {
                setActiveTab('saved');
                fetchSavedReports();
              }}
            >
              Saved Reports
            </Tab>
          </Tabs>
          
          {renderTabContent()}
        </>
      )}
    </PageContainer>
  );
};

export default ReportingAI;