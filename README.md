# InvertedIndex
This project implements an inverted index data structure for information retrieval. The InvertedIndex class provides methods for building the index, calculating term frequency, document frequency, cosine similarity, and performing TF-IDF calculations.

## The key features of this project include:

Building the Inverted Index: The *readFiles()* method reads text files from a specified directory and processes each file to build the inverted index. It calculates the term frequency, document frequency, and updates the index accordingly.

Cosine Similarity Calculation: The computeCosineSimilarity() method computes the cosine similarity between a given query and the indexed documents. It uses the TF-IDF (Term Frequency-Inverse Document Frequency) approach to calculate the similarity values.

Ranking Documents: The rankDocuments() method ranks the documents based on their cosine similarity values in descending order. It returns a list of ranked documents.

TF-IDF Calculation: The calculateTFIDF() method calculates the TF-IDF values for the query terms. It uses the IDF (Inverse Document Frequency) measure to determine the importance of each term.

Web Crawling: The getPageLinks() method retrieves web page links by crawling a specified URL and its linked pages. It uses the Jsoup library to fetch HTML code and parse it to extract links.

To use this project, you can create an instance of the InvertedIndex class and call its methods accordingly. The provided main() function demonstrates an example usage, including reading files, computing cosine similarity, ranking documents, and calculating TF-IDF for a given query.
