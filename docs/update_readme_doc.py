import sys
from os import listdir
from os.path import isfile, join

onlyfiles = [f for f in listdir(sys.argv[1]) if (isfile(join(sys.argv[1], f)) & f.endswith(".html"))]

mainpage_template="""
## Available stream data

{}

### Support or Contact

[Issues](https://github.com/indyMccarthy/Mimir-Event-Interface/issues)
"""
# Declared on registry and topic name associated
schema_available_list=""

for f in onlyfiles:
    schema_available_list += '- [{}]({})\n'.format(f.rsplit('.html', 1)[0], 'schemas/{}'.format(f))

readme=mainpage_template.format(schema_available_list)

print(readme)