import os
import glob
import json


def convert_file(filepath: str) -> None:
    with open(filepath, 'r') as f:
        contents = json.load(f)

    
    new_filepath = filepath[:filepath.find('.json')] + "_.json"
    print(new_filepath)
    with open(new_filepath, 'w') as f:
     f.write(contents)
      #json.dump(fileJson, f)




if __name__ == "__main__":
    filepaths = glob.glob(os.getcwd() + "/../resources/TextFiles/Dialogue/*/*.json")
    
    for filepath in filepaths:
        print(filepath)
        convert_file(filepath)
