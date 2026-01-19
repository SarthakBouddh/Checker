import { useState } from "react";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export default function ExcelUpload() {
  const [file, setFile] = useState(null);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const uploadFile = async () => {
    if (!file) {
      setError("Please select an Excel file");
      return;
    }

    setError("");
    setLoading(true);
    setData([]);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch(`${API_BASE_URL}/api/files/upload`, {
        method: "POST",
        body: formData,
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Upload failed");
      }

      const result = await res.json();
      setData(result);
    } catch (err) {
      setError(err.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  // 🔄 RESET HANDLER
  const resetAll = () => {
    setFile(null);
    setData([]);
    setError("");
  };

  // 🎨 HIGH-CONTRAST ROW COLORS
  const rowStyle = (status) => {
    switch (status) {
      case "VALID":
        return { backgroundColor: "#e6fffa", color: "#065f46" }; // green
      case "CORRECTED":
        return { backgroundColor: "#fff7ed", color: "#9a3412" }; // orange
      case "INVALID":
        return { backgroundColor: "#fee2e2", color: "#991b1b" }; // red
      default:
        return {};
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1200px", margin: "auto" }}>
      <h2>Excel Distance Validator</h2>

      <input
        type="file"
        accept=".xlsx,.xls"
        onChange={(e) => setFile(e.target.files[0])}
      />

      <button
        onClick={uploadFile}
        disabled={!file || loading}
        style={{ marginLeft: "10px" }}
      >
        {loading ? "Uploading..." : "Upload"}
      </button>

      {/* 🔄 RESET BUTTON */}
      <button
        onClick={resetAll}
        disabled={loading}
        style={{ marginLeft: "10px" }}
      >
        Reset
      </button>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {data.length > 0 && (
        <table
          border="1"
          cellPadding="8"
          style={{
            marginTop: "20px",
            width: "100%",
            borderCollapse: "collapse",
          }}
        >
          <thead style={{ backgroundColor: "#f1f5f9", color: "#0f172a" }}>
            <tr>
              <th>Row</th>
              <th>Start</th>
              <th>End</th>
              <th>Distance</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {data.map((r, index) => (
              <tr key={index} style={rowStyle(r.status)}>
                <td>{r.row}</td>
                <td>{r.start}</td>
                <td>{r.end}</td>
                <td>{r.distance}</td>
                <td>{r.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
