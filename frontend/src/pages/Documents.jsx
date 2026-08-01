import { useEffect, useState } from 'react';
import { documentService } from '../services/api';

export default function Documents() {
    const [documents, setDocuments] = useState([]);
    const [file, setFile] = useState(null);
    const [documentType, setDocumentType] = useState('POLICY_DOCUMENT');
    const [policyId, setPolicyId] = useState('');
    const [error, setError] = useState('');

    const loadDocs = () => {
        documentService.getMine().then(res => setDocuments(res.data)).catch(() => {});
    };

    useEffect(() => { loadDocs(); }, []);

    const handleUpload = async (e) => {
        e.preventDefault();
        setError('');
        if (!file) { setError('Please choose a file'); return; }

        const formData = new FormData();
        formData.append('file', file);
        formData.append('documentType', documentType);
        if (policyId) formData.append('policyId', policyId);

        try {
            await documentService.upload(formData);
            setFile(null);
            loadDocs();
        } catch (err) {
            setError(err.response?.data?.error || 'Upload failed');
        }
    };

    const handleDownload = async (id, fileName) => {
        const res = await documentService.download(id);
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        link.remove();
    };

    return (
        <div className="container">
            <h1 style={{ marginBottom: 20 }}>Documents</h1>

            <div className="card">
                <h3 style={{ marginBottom: 12 }}>Upload Document</h3>
                {error && <p style={{ color: 'red', marginBottom: 10 }}>{error}</p>}
                <form onSubmit={handleUpload}>
                    <div style={{ marginBottom: 10 }}>
                        <input type="file" onChange={(e) => setFile(e.target.files[0])} required />
                    </div>
                    <div style={{ marginBottom: 10 }}>
                        <select value={documentType} onChange={(e) => setDocumentType(e.target.value)}>
                            <option value="POLICY_DOCUMENT">Policy Document</option>
                            <option value="CLAIM_ATTACHMENT">Claim Attachment</option>
                            <option value="ID_PROOF">ID Proof</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                    <div style={{ marginBottom: 14 }}>
                        <input placeholder="Policy ID (optional)" value={policyId}
                            onChange={(e) => setPolicyId(e.target.value)} />
                    </div>
                    <button type="submit">Upload</button>
                </form>
            </div>

            <div className="card">
                <table>
                    <thead>
                        <tr><th>File Name</th><th>Type</th><th>Uploaded</th><th>Action</th></tr>
                    </thead>
                    <tbody>
                        {documents.map(d => (
                            <tr key={d.id}>
                                <td data-label="File Name">{d.fileName}</td>
                                <td data-label="Type">{d.documentType}</td>
                                <td data-label="Uploaded">{d.uploadedOn}</td>
                                <td data-label="Action">
                                    <button style={{ width: 'auto', padding: '6px 12px' }}
                                        onClick={() => handleDownload(d.id, d.fileName)}>Download</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                {documents.length === 0 && <p style={{ padding: 20, textAlign: 'center', color: '#888' }}>No documents uploaded yet.</p>}
            </div>
        </div>
    );
}