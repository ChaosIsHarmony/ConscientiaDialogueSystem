import json
import sys

filepath = "./resources/TextFiles/Templates/PlayerSaveTemplate.json"


def get_event_description_by_event_num(triggeredEvents: list, eventNum: str) -> str:
    for event in triggeredEvents:
        if eventNum in dict.keys(event):
            return event[eventNum]["description"]



if __name__ == "__main__":
    flags = [flag for flag in sys.argv if "-" in flag]
    if len(flags) == 0:
        print("""
                python3 <prog>.py <src file> [options]
                    -gd <eventNum>    Get description for event num
                """)
        exit()
    

    if ".py" in sys.argv[1]:
        filepath = sys.argv[1]

    with open(filepath) as f:
        triggeredEvents = json.load(f)["triggered_events"]

    for flag in flags:
        if flag == "-gd":
            eventNum = sys.argv[sys.argv.index("-gd")+1]
            print(get_event_description_by_event_num(triggeredEvents, eventNum))


