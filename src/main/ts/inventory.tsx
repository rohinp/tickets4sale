import axios from 'axios';
import { date, string } from 'fp-ts';
import React, { Component, ChangeEvent } from 'react';

interface InventoryState {
    showDate: Date;
    queryDate: Date;
    inventories: Inventories;
}

interface Show {
    title: string;
    ticketsLeft: number;
    ticketsAvailable: number;
    status: string;
    favrouite:boolean;
}

interface Inventory {
    genre: string;
    price: number;
    shows: [Show];
}
interface Inventories {
    inventory: [Inventory] | [];
}
class Inventory extends Component<unknown, InventoryState> {

    constructor(props: unknown) {
        super(props);
        this.state = {
            showDate: new Date(),
            queryDate: new Date(),
            inventories: { inventory: [] }
        }
    }

    private dateString(dd: Date) {
        const d = dd.toISOString().split("T")
        return d[0];
    }

    private createInventoryTable(inventory: Inventories) {
        const header = (
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Tickets Left</th>
                    <th>Tickets Available</th>
                    <th>Status</th>
                    <th>Price</th>
                    <th>Favrouite</th>
                </tr>
            </thead>
        )

        const rows = (shows: [Show], price: number) => (
            shows.map((s, index) => (
                <tr key={s.title + index}>
                    <td>{s.title}</td>
                    <td>{s.ticketsLeft}</td>
                    <td>{s.ticketsAvailable}</td>
                    <td>{s.status}</td>
                    <td>{price}</td>
                    <tr>
                        <fieldset className="form-group">
                            <label className="paper-switch-2">
                                <input 
                                    id="paperSwitch10" 
                                    name="paperSwitch10" 
                                    type="checkbox" 
                                    defaultChecked={s.favrouite}
                                    onChange={e => this.checkFav(e, s.title)}/>
                                <span className="paper-switch-slider round"></span>
                            </label>
                            <label htmlFor="paperSwitch10" className="paper-switch-2-label">
                                
                            </label>
                        </fieldset>
                    </tr>
                </tr>
            ))
        )
        return (
            <div>
                {inventory.inventory.map((i, index) => (
                    <div key={i.genre + index} className="paper container" >
                        <h3>{i.genre}</h3>
                        <table className="table">
                            {header}
                            <tbody>
                                {rows(i.shows, i.price)}
                            </tbody>
                        </table>
                    </div>
                ))}
            </div>
        )
    }

    private inventory(showDate: Date, queryDate?: Date) {
        const s = this.dateString(showDate)
        const q = (queryDate ? this.dateString(queryDate) : this.dateString(new Date()))
        const url = "http://localhost:8080/show?show-date=" + s + "&query-date=" + q
        axios.get(url).then(
            d => {
                try {
                    console.log(d.data)
                    const i: Inventories = d.data
                    console.log(i)
                    this.setState({
                        inventories: i
                    })
                } catch (error) {
                    console.log(error)
                }
            },
            err => console.log(err)
        );
    }

    private checkFav(event:ChangeEvent<HTMLInputElement>, item:string) {
        const url = "http://localhost:8080/fav"
        axios.post(url, {title: item, isFav: event.target.value == "on"},{
            headers: {'content-type': 'application/json'}
          }).then(
            d => {
                this.inventory(this.state.showDate, this.state.queryDate)
                console.log("Docs updates " + d)
            },
            err => console.log(err)
        );
    }

    private handleDateChange() {
        this.inventory(this.state.showDate, this.state.queryDate)
    }

    render() {
        return (
            <div>
                <div className="row paper container flex-center" >
                    <div className="sm-4 col">
                        Show date.
                        <input
                            type="date"
                            className="paper-btn"
                            id="showDate"
                            onChange={event => this.setState({ showDate: new Date(event.target.value) })}
                            value={this.dateString(this.state.showDate)} />
                    </div>
                    <div className="sm-4 col">
                        [Query date, default=now()]
                        <input
                            type="date"
                            className="paper-btn"
                            id="queryDate"
                            onChange={event => this.setState({ queryDate: new Date(event.target.value) })}
                            value={this.dateString(this.state.queryDate)} />
                    </div>
                    <div className="sm-4 col">
                        <br />
                        <button onClick={e => this.handleDateChange()}>Load Data</button>
                    </div>

                </div>
                {this.createInventoryTable(this.state.inventories)}
            </div>
        );
    }

}

export default Inventory