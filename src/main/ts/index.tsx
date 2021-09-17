import React, { FC } from "react";
import ReactDOM from "react-dom";
import FileUpload from "./fileupload";
import Inventory from "./inventory";
import {NotificationContainer} from 'react-notifications';

const App: FC = () => (
  <div>
      <NotificationContainer/>
      <h1 className="shadow shadow-hover">Tickets 4 sale</h1>
      <FileUpload />
      <Inventory />
  </div>
);

ReactDOM.render(<App />,document.getElementById("root"));