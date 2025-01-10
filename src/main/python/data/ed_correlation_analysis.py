import os
import sys
import json
import pandas as pd

def correlation_analysis(dataname) :
    try :
        file_dir = os.path.dirname(__file__)
        file_path = os.path.join(file_dir, 'processed', 'fire_statistics', f'{dataname}.csv')
        df = pd.read_csv(file_path, encoding='EUC-KR')
        df.fillna('미확인', inplace=True)
        # df = df.isnull().sum()

        electric_cause_df = df[df['발화요인대분류'] == '전기적 요인']
        print (electric_cause_df)
        # 장소대분류별 평균 재산피해소계 계산
        grouped = electric_cause_df.groupby('장소대분류')['재산피해소계'].mean()
        print(grouped)

        exit()
        # electric_cause_df = df[df['발화요인대분류'] == '전기적요인']
    except Exception as e:
        print (f"Error processing {file_path} : {e}")
        return json.dump({"error": str (e)}, ensure_ascii=False)

def execute_function (function_name, args) :
    functions = {
        "correlation_analysis" : correlation_analysis
    }

    if function_name in functions:
        try :
            result = functions[function_name](*args)
            print (result)
        except Exception as e:
            print (f"Error executing function  '{function_name}' : {str(e)}")
    else :
        print(f"Error: Function '{function_name}' not found.")

# if __name__ == "__main__":
#     if len(sys.argv) < 2: 
#         print("Useage: python fileName.py <function_name> [args...]")
#         sys.exit(1)
#     function_name = sys.argv[1]
#     args = sys.argv[2:]
#     execute_function(function_name, args)


correlation_analysis(2016)