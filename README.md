# Community Service Request System (Console + SQLite)
Run `com.scc.Main`. Database: CommunityService.db (open in Letos).
The Main run configuration already has --enable-native-access=ALL-UNNAMED (removes the warnings).

Accounts: admin@community.com/admin123 | worker@community.com/worker123 | resident@community.com/resident123

Flow: Resident submits request -> Admin assigns it to a Field Worker -> Worker starts + resolves it
-> Resident tracks status. Admin sees summary report and audit log.
