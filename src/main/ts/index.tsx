import React, { FC , Component} from "react";
import ReactDOM from "react-dom";
import FileUpload from "./fileupload";
import Inventory from "./inventory";

const App: FC = () => (
  <div>
      <h1 className="shadow shadow-hover">Tickets 4 sale</h1>
      <FileUpload />
      <Inventory />
  </div>
);

ReactDOM.render(<App />,document.getElementById("root"));