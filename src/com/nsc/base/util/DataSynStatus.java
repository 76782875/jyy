package com.nsc.base.util;

public enum DataSynStatus {
	ARCHIVE{//�鵵�ļ�
		@Override
		public String toString() {
			return "L09";
		}
	}, 
	UPDATE{//�����ļ�,���path��Ϊ�գ�Ϊ�ļ�Ǩ��
		@Override
		public String toString() {
			return "L04";
		}
	},  
	REMOVE{
		@Override
		public String toString() {
			return "L05";
		}
	},
	DELETE{//ɾ���ļ�
		@Override
		public String toString() {
			return "L07";
		}
	},  
	DESTROY{//�����ļ�
		@Override
		public String toString() {
			return "L03";
		}
		
	}, 
	RESTORE{//�ָ��ļ�
		@Override
		public String toString() {
			return "L08";
		}
	},
	INSERT{//����
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "L06";
		}
	}
}
