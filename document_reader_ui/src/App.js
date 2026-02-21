import './App.css';
import DocumentUpload from "./components/DocumentUpload";

function App() {
    const documents = useSelector(state => state.documents.items);
  return (
    <div className="App">
        <h1>Upload Document</h1>
      <DocumentUpload onUpload={(data) => console.log('Uploaded:', data)} />
    </div>
  );
}

export default App;
