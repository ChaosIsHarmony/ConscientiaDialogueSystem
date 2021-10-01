import os
import glob
import json

def convert_file(filepath: str) -> None:
    with open(filepath, 'r') as f:
        contents = json.load(f)

    dialogueJson = contents.get("dialogue")
    for key in dialogueJson.keys():
        if "action" in dialogueJson[key]:
            action = {}
            action["type"] = dialogueJson[key]["action"][0]
            if action["type"] == '^':
                action["event_num"] = dialogueJson[key]["action"][1]
                action["dest_add"] = dialogueJson[key]["action"][2]
            else:
                action["target_add"] = dialogueJson[key]["action"][1]
            
            dialogueJson[key]["action"] = action
            print(dialogueJson[key]["action"])
    
    
    new_filepath = filepath[:filepath.find('.json')] + "_.json"
    print(new_filepath)
    with open(new_filepath, 'w') as f:
        #f.write(contents)
        json.dump(contents, f)




if __name__ == "__main__":
    filepaths = glob.glob(os.getcwd() + "/../resources/TextFiles/Dialogue/*/*.json")
    
    for filepath in filepaths:
        print(filepath)
        if "SANCTUARY.json" not in filepath and "_.json" not in filepath:
            convert_file(filepath)
