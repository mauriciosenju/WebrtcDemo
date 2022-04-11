import logo from './logo.svg';
import './App.css';
import { ListBox } from 'primereact/listbox';
import React,{Component} from 'react';
import "primereact/resources/themes/lara-light-indigo/theme.css";  //theme
import "primereact/resources/primereact.min.css";                  //core css
import "primeicons/primeicons.css";
 class App extends Component { 

  constructor() {
    super();
    this.state = {
    sock:null,
    me:null,
    list:[],
    text:false,
    btn:true,
    elem:{name:"", code:""},
    selected:{ name:"", code:""},
    dataChannel:null,
    connect:false,
    pc:new RTCPeerConnection({
      'iceServers': [{
        'url': 'stun:stun.example.org'
      }]
    }, {
      optional : [ {
          RtpDataChannels : true
      } ]
  })
    };
    this.offer = this.offer.bind(this)
    this.sendmessage  = this.sendmessage.bind(this)
}
componentDidMount(){
  var sock = this.state.sock
  sock = new WebSocket("ws://localhost:8080/peerjs")
 
   var pc =  this.state.pc
   var list = this.state.list
   var elem = this.state.elem
   var n = list.length

 pc.ondatachannel=(e)=>{
  
  e.channel.onopen= async (i)=> {
    console.log('datachannel open');
    this.setState({text:true,btn:false})
  }
  this.setState({dataChannel:e.channel})
   e.channel.onmessage=function(evn){
    console.log('datachannel message recive', evn);
  }
 };
 
  sock.onopen = function(e) {
    
  console.log('open', e);
  }
  sock.onclose = function(e) {
    console.log('close', e);
    
  }
  sock.onerror = function(e) {
    console.log('error', e);
  }

  sock.onmessage = async (e)=> {
    console.log('message', e.data);
    var message = JSON.parse(e.data);
    if (message.type === 'offer') {
      pc.setRemoteDescription(new RTCSessionDescription(message.payload.sdp));
      pc.createAnswer(function(answer) {
        pc.setLocalDescription(answer);
        sock.send( JSON.stringify({
          type: "answer",
          payload: {
            sdp:answer,
            type: "answer",
            connectionId:"nose12345"
             },
            dst:message.sender,
            sender: 'JSESSIONID'
         }));
}, function(error) {
  console.log('error creating answer');
   
});
list.map((elemt)=>{
  console.log(elemt);
 if(elemt.code===message.sender){
  this.setState({selected:elemt})
 }

 }); 

        } else if (message.type === 'candidate'){
         pc.addIceCandidate(new RTCIceCandidate(message.payload.sdp));
        }else if(message.type === 'answer'){
        pc.setRemoteDescription(new RTCSessionDescription(message.payload.sdp));

      }else if(message.type === 'OLD'){
        elem={name:"guest"+n,code:message.sender}
         list.push(elem)
         
         this.setState({list:list,me:message.dst})
        
      
      }else if(message.type === 'NEW'){
        elem={name:"guest"+n,code:message.sender}
         list.push(elem)
         
         this.setState({list:list,me:message.dst})
        
      }else if(message.type === 'LEAVE'){
       
        let ind =99
       list.map((elemt,index)=>{
        console.log(elemt);
       if(elemt.code===message.sender){
        ind=index;
       
        list.splice(ind,1)
       
       this.setState({list:list,me:message.dst})
       }
    
       });      
      
      }
      
    }

  this.setState({sock:sock})

}
sendmessage(){
  var dataChannel =  this.state.dataChannel
  console.log("sending message......");
  dataChannel.send("message")
   
}
offer(){
  var btn = this.state.btn
  var text = this.state.text
  var sock = this.state.sock
  var username =this.state.selected.code
    var pc =  this.state.pc
    console.log("Connecting with other peer......");
  
  var dataChannel = pc.createDataChannel("dataChannel", { reliable: true });
  console.log("creating datachannel....");
  
  dataChannel.onerror = function(error) {
    console.log("Error:", error);
};
dataChannel.onclose = function(e) {
  
    console.log("Data channel is closed");
};
dataChannel.onmessage=  function(e) {
  console.log("Data channel recive ",e);
  
};
    // send any ice candidates to the other peer

    pc.createOffer(function(offer) {
      console.log("creating offer to "+username);
      console.log("waiting for answer..... ");
     
      sock.send( JSON.stringify({
        type: "offer",
        payload: {
          sdp: offer,
          type: "offer",
          connectionId:"nose12345"
           },
          dst:username,
          sender: 'JSESSIONID'
        
      }));
      pc.setLocalDescription(offer);
     
  }, function(error) {
    console.log('error creating offer');
   
  });
  btn=false
  text=true
    pc.onicecandidate = function (evt) {
      console.log("sending iceCandidates to "+username);
  
      if (evt.candidate) {
        sock.send( JSON.stringify({
          type: "candidate",
          payload: {
            sdp: evt.candidate,
            type: "candidate",
            connectionId:"nose12345"
             },
            dst:username,
            sender: 'JSESSIONID'
      }));
         
      }
    };

    // once remote stream arrives, sho480w it in the remote video element
  this.setState({dataChannel:dataChannel,btn:btn,text:text})
}

render() {
  
  
  return (
    <div className="App">
      <header className="App-header">
        <input type="text" name="username" disabled={this.state.text}   value={this.state.selected.name} onChange={(e)=>this.setState({user:e.target.value})}/>
        <button onClick={this.offer} >Connect</button>
      <p>
         {this.state.connected}
        </p>
        <div className="card">
                <h5>Users</h5>
                <ListBox value={this.state.selected} options={this.state.list} onChange={(e) => this.setState({ selected: e.value })} optionLabel="name" style={{ width: '15rem' }} />
            </div>
            <button disabled={this.state.btn} onClick={this.sendmessage} >Send message</button>
      </header>
     
     
    </div>
  );
}
 }export default App;
