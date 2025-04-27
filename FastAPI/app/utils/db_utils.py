def format_db_query_result(query_result: dict):
    print(query_result)
    context = "\n".join(query_result['documents'][0])
    print(f"\n\nContext = {context}")
    return context