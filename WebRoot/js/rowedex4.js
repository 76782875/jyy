jQuery("#rowed4").jqGrid({
	//·��
   	url:'server.php?q=2',
	//���������
	datatype: "json",
	//��������
   	colNames:['Inv No','Date', 'Client', 'Amount','Tax','Total','Notes'],
	//�е�����   ����  �п�  ���뷽ʽ  �Ƿ���Ա༭  
   	colModel:[
   		{name:'id',index:'id', width:55},
   		{name:'invdate',index:'invdate', width:90, editable:true},
   		{name:'name',index:'name', width:100,editable:true},
   		{name:'amount',index:'amount', width:80, align:"right",editable:true},
   		{name:'tax',index:'tax', width:80, align:"right",editable:true},		
   		{name:'total',index:'total', width:80,align:"right",editable:true},		
   		{name:'note',index:'note', width:150, sortable:false,editable:true}		
   	],
	//һҳ��ʾ������
   	rowNum:10,
	//
	rowList:[10,20,30],
	//ҳ��
   	pager: '#prowed4',
	//����ʲô����
   	sortname: 'id',
    viewrecords: true,
	//Ĭ��˳��
    sortorder: "desc",
	//�༭���ļ�����
	editurl: "server.php",
	caption: "Full control"
});
jQuery("#ed4").click( function() {
	jQuery("#rowed4").jqGrid('editRow',"13");
	//���ĵ�ǰ���Ϊ�����ܱ༭
	this.disabled = 'true';
	jQuery("#sved4").attr("disabled",false);
});
jQuery("#sved4").click( function() {
	jQuery("#rowed4").jqGrid('saveRow',"13", checksave);
	//���ĵ�ǰ������ʽ,Ϊ���ɱ���
	jQuery("#sved4").attr("disabled",true);
	jQuery("#ed4").attr("disabled",false);
});
//����ķ���
function checksave(result) {
	if (result.responseText=="") {alert("Update is missing!"); return false;}
	return true;
}