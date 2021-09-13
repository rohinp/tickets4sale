import React from "react";
import ReactDOM from "react-dom";

const App = () => (
  <div>
    <form action="/inventory" method="put" encType="multipart/form-data">
      <input type="file" id="myFile" name="dateFile" />
      <input type="submit" />
    </form>
  </div>
);

ReactDOM.render(
  <App />,
  document.getElementById("root")
);