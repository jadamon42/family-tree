import { MemoryRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from './views/HomePage';
import './App.css';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
      </Routes>
    </Router>
  );
}
