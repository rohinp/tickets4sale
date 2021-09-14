import React, { FC , Component} from "react";
import ReactDOM from "react-dom";
import FileUpload from "./fileupload";

const App: FC = () => (
  <div>
    <div className="paper container">
      <h1 className="shadow shadow-hover">Tickets 4 sale</h1>
      <FileUpload />
      <div className="row paper container flex-center" >
        <div className="sm-4 col">
          Show date.
          <input type="date" className="paper-btn" name="showDate" />
        </div>
        <div className="sm-4 col">
          [Query date, default=now()]
          <input type="date" className="paper-btn" name="queryDate" />
        </div>
      </div>
      <div className="paper container" >
        <h3 >DRAMA</h3>
        <table className="table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Tickets Left</th>
              <th>Tickets Available</th>
              <th>Status</th>
              <th>Price</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>no</td>
              <td>Content</td>
              <td>available</td>
              <td>to</td>
              <td>show</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div className="paper container" >
        <h3 >Musical</h3>
        <table className="table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Tickets Left</th>
              <th>Tickets Available</th>
              <th>Status</th>
              <th>Price</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>no</td>
              <td>Content</td>
              <td>available</td>
              <td>to</td>
              <td>show</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
);

ReactDOM.render(<App />,document.getElementById("root"));