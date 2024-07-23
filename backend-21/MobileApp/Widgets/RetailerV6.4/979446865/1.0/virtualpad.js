function loadVirtualKeyPad()
{
	var anchorDisplay = new Array() ;
	var str = "";
	anchorDisplay[0] = "<a  width='30%' id='1'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=1'  style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>1</span></a>" ;
	anchorDisplay[1] = "<a  width='30%' id='2'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=2' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>2</span></a>" ;
	anchorDisplay[2] = "<a  width='30%' id='3'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=3' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>3</span></a>" ;
	anchorDisplay[3] = "<a  width='30%' id='4'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=4' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>4</span></a>" ;
	anchorDisplay[4] = "<a  width='30%' id='5'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=5' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>5</span></a>" ;
	anchorDisplay[5] = "<a  width='30%' id='6'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=6' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>6</span></a>" ;
	anchorDisplay[6] = "<a  width='30%' id='7'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=7' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>7</span></a>" ;
	anchorDisplay[7] = "<a  width='30%' id='8'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=8' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>8</span></a>" ;
	anchorDisplay[8] = "<a  width='30%' id='9'  class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=9' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>9</span></a>" ;
	clearBtn = "<a  width='30%' id='clr'  class='c3lMenuGroup'  href='keypad://clearall?name=$pin$' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>CLR</span></a>" ;
	anchorDisplay[9] = "<a  width='30%' id='10' class='c3lMenuGroup'  href='keypad://append?name=$pin$&value=0' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>0</span></a>" ;
	deleteBtn = "<a  width='30%' id='del'  class='c3lMenuGroup'  href='keypad://clearchar?name=$pin$' style='background-color:white;margin:2px 2px 2px 2px'><span align='center' style='color:black;'>DEL</span></a>" ;
	
	//var urlencodedpin = widget.urlEncode("name=$pin$&value="+eKey+"&pinlength=$pinlength$");//$%nsi://"+urlencodedpin+"%,
						
	str = [str,"<br/><div align='left' width='90%'><span style='color:white;padding-left:15px'>Please Enter Pin:</span></div><br/><input align='center' width='90%' type='password' id='pin' name='pin' value='' emptyok='false' maxlength='"+PIN_LENGTH+"' size='"+PIN_LENGTH+"' readonly /><br/><div class='c3lMenuGroup'><input align='center' width='40%' type='numeric' id='amount' name='amount' value='' emptyok='false' maxlength='"+PIN_LENGTH+"' size='"+PIN_LENGTH+"' readonly /><input align='center' width='40%' type='numeric' id='amount' name='amount' value='' emptyok='false' maxlength='"+PIN_LENGTH+"' size='"+PIN_LENGTH+"' readonly /></div><br/><div width='90%'  style='background-color:black' align='center' class='c3lMenuGroup'>"+anchorDisplay[0]+anchorDisplay[1]+anchorDisplay[2]+anchorDisplay[3]+anchorDisplay[4]+anchorDisplay[5]+anchorDisplay[6]+anchorDisplay[7]+anchorDisplay[8]+clearBtn+anchorDisplay[9]+deleteBtn+"</div><br/><br/><div align='center'><a   class='c3lMenuGroup payBtn' width='30%' id='submit' align='center' href=\"javascript:pinCardPayment('false','0',$pinlength);\" style='background-color:white;'><span align='center' style='color:black;'>Submit</span></a></div>"].join("");
					
	/*var divElement = document.getElementById("vkey");
	divElement.style.display = "block" ;
	divElement.style.backgroundColor = 'black';
    divElement.innerHTML = "<setvar name='pin' value=''/><setvar name='pinlength' value=''/>"+ str;*/
	//return str;
}


