import { HashRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from './views/HomePage';
import PersonFormPage from './views/PersonFormPage';
import './App.css';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/add-person" element={<PersonFormPage />} />
      </Routes>
    </Router>
  );
}
