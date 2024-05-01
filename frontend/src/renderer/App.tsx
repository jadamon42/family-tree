import { HashRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from './views/HomePage';
import PersonFormPage from './views/PersonFormPage';
import './App.css';
import PartnerFormPage from './views/PartnerFormPage';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/person-form" element={<PersonFormPage />} />
        <Route path="/partner-form" element={<PartnerFormPage />} />
      </Routes>
    </Router>
  );
}
