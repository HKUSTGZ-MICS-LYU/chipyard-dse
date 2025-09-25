import os
import sys
import json
import pathlib

filelist = sys.argv[1]
output = sys.argv[2]
hierarchy = sys.argv[3] if len(sys.argv) > 3 else None
topname = sys.argv[4] if len(sys.argv) > 4 else None

def find_topname(hier):
    if hier["module_name"] == topname:
        return hier
    else:
        for instance in hier["instances"]:
            result = find_topname(instance)
            if result:
                return result
    return None

def dump_all_modules(hier):
    modules = []
    def recurse(h):
        modules.append(h["module_name"])
        for instance in h["instances"]:
            recurse(instance)
    recurse(hier)
    return modules

if hierarchy:
    with open(hierarchy) as f:
        hier = json.load(f)
    if topname:
        hier = find_topname(hier)
        if not hier:
            print(f"Top module {topname} not found in hierarchy.")
            sys.exit(1)
    all_modules = set(dump_all_modules(hier))
    
with open(filelist) as f:

    files = [line.strip() for line in f if line.strip()]
    if not files:
        print("No files to merge.")
        sys.exit(1)
    for file in files:
        if not os.path.isfile(file):
            print(f"File not found: {file}")
            sys.exit(1)
    with open(output, 'wb') as outfile:
        for fname in files:
            if hierarchy:
                modname = pathlib.Path(fname).stem
                if (modname not in all_modules) and ("mems" not in modname):
                    continue
            with open(fname, 'rb') as infile:
                outfile.write(infile.read())
    print(f"Merged {len(files)} files into {output}")

    if not os.path.isfile(output):
        print(f"Output file could not be created: {output}")
        sys.exit(1)
    if os.path.getsize(output) == 0:
        print(f"Output file is empty: {output}")
        sys.exit(1)
    print(f"Output file size: {os.path.getsize(output)} bytes")


