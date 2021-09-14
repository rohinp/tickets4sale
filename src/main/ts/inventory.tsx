import axios from 'axios';
import React, { Component, ChangeEvent } from 'react';
import {NotificationContainer, NotificationManager} from 'react-notifications';

class Inventory extends Component {

    /* private getFileFromInput(file: File): Promise<any> {
        return new Promise(function (resolve, reject) {
            const reader = new FileReader();
            reader.onerror = reject;
            reader.onload = function () { resolve(reader.result); };
            reader.readAsBinaryString(file); // here the file can be read in different way Text, DataUrl, ArrayBuffer
        });
    } */

    private manageUploadedFile(file: File) {
        const formData = new FormData(); 
     
        formData.append( 
            "dataFile", 
            file, 
            file.name
        ); 
        axios.put("http://localhost:8080/inventory",formData).then(
            _ => NotificationManager.success('Success', 'Upload File'),
            err => NotificationManager.error('Error', 'Click me!', 5000, () => {
                alert(err);
              })
        );
    }

    private handleFileChange(event: ChangeEvent<HTMLInputElement>) {
        event.persist();
        const file = (event.target.files as FileList)[0]
        if (file) {
            this.manageUploadedFile(file);
        }
    }

    render() {
        return (
            <div className="paper container">
                Uplod a csv file Record[title,yyyy-mm-dd,genre]<br />
                <input accept=".csv" type="file" id="file" className="paper-btn" name="dateFile" onChange={e => this.handleFileChange(e)} />
                <NotificationContainer/>
                {/* <button className="paper-btn" onClick={e => e.stopPropagation()}>Upload File</button> */}
            </div>
        );
    }

}

export default Inventory