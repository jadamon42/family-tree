import { HashRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from './views/HomePage';
import PersonFormPage from './views/PersonFormPage';
import PartnerFormPage from './views/PartnerFormPage';
import './App.css';
import ChildFormPage from './views/ChildFormPage';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/person-form" element={<PersonFormPage />} />
        <Route path="/partner-form" element={<PartnerFormPage />} />
        <Route path="/child-form" element={<ChildFormPage />} />
      </Routes>
    </Router>
  );
}
