import os
import sys
import json
import pandas as pd

def read_fire_statistics_csv(dataname):
    try :
        file_dir = os.path.dirname(__file__)
        file_path = os.path.join(file_dir, 'processed', 'fire_statistics', f'{dataname}')
        
        if not os.path.exists(file_path):
            raise FileNotFoundError(f'파일을 찾을 수 없습니다: {file_path}')
        
        try:
            df = pd.read_csv(file_path + ".csv", encoding='EUC-KR')
        except UnicodeDecodeError:
            df = pd.read_excel(file_path + ".xlsx", encoding='utf-8')
        except Exception:
            raise Exception(f'파일을 읽을 수 없습니다: {file_path}')
        
        df.fillna("미확인", inplace=True)
        
        
        
    except Exception as e :
        print(f'Error processing {file_path} : {e}')
        return json.dumps({"error": str(e)}, ensure_ascii=False)